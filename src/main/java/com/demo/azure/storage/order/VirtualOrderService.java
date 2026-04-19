package com.demo.azure.storage.order;

import com.demo.azure.storage.StoragePathProvider;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class VirtualOrderService {

    private static final DateTimeFormatter DAY = DateTimeFormatter.ISO_LOCAL_DATE;

    private final StoragePathProvider pathProvider;
    private final XmlMapper xmlMapper;

    public VirtualOrderService(StoragePathProvider pathProvider) {
        this.pathProvider = pathProvider;
        this.xmlMapper = new XmlMapper();
        this.xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true);
    }

    public VirtualOrderResponse createVirtualOrderXml(VirtualOrderRequest request) throws IOException {
        LocalDate date = request.getOrderDate() != null ? request.getOrderDate() : LocalDate.now();
        String orderNo =
                request.getOrderNo() != null && !request.getOrderNo().isBlank()
                        ? request.getOrderNo().trim()
                        : generateOrderNo(date);

        VirtualOrderXmlPayload payload = new VirtualOrderXmlPayload();
        payload.setOrderNo(orderNo);
        payload.setAmount(request.getAmount().setScale(2, RoundingMode.HALF_UP));
        payload.setUserName(request.getUserName().trim());
        payload.setPhone(request.getPhone().trim());
        payload.setOrderDate(date.format(DAY));
        payload.setPaymentMethod(request.getPaymentMethod().trim());

        Path dayDir = pathProvider.root().resolve(date.format(DAY));
        Files.createDirectories(dayDir);

        String fileBase = toSafeFileBase(orderNo);
        String fileName = fileBase + ".xml";
        Path file = dayDir.resolve(fileName);

        xmlMapper.writeValue(file.toFile(), payload);

        Path root = pathProvider.root().normalize();
        String relative = root.relativize(file.toAbsolutePath().normalize()).toString().replace('\\', '/');

        VirtualOrderResponse res = new VirtualOrderResponse();
        res.setOrderNo(orderNo);
        res.setAbsolutePath(file.toAbsolutePath().normalize().toString());
        res.setRelativeToStorageRoot(relative);
        res.setFileName(fileName);
        return res;
    }

    private static String generateOrderNo(LocalDate date) {
        return "ORD-" + date.format(DateTimeFormatter.BASIC_ISO_DATE) + "-" + randomSuffix();
    }

    private static String randomSuffix() {
        int n = ThreadLocalRandom.current().nextInt(1_000_000);
        return String.format("%06d", n);
    }

    /**
     * File name stem must match the order number; strip characters invalid on Windows / POSIX file names.
     */
    private static String toSafeFileBase(String orderNo) {
        if (orderNo == null || orderNo.isBlank()) {
            return "order-unknown";
        }
        String s = orderNo.trim();
        s = s.replaceAll("[<>:\"/\\\\|?*\\x00-\\x1f]", "_");
        s = s.replaceAll("[\\s.]+$", "");
        if (s.isEmpty()) {
            return "order-unknown";
        }
        if (s.length() > 180) {
            s = s.substring(0, 180);
        }
        return s;
    }
}
