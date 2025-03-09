package com.example.osgi.consumer.delivery;


public class DeliveryConsumer implements BundleActivator  {
    @Override
    public void start(BundleContext context) throws Exception {
        System.out.println("Delivery Client: Looking for services...");

        // Get DeliveryService
        ServiceReference<DeliveryService> deliveryRef = context.getServiceReference(DeliveryService.class);
        // Get NotificationService
        ServiceReference<NotificationService> notificationRef = context.getServiceReference(NotificationService.class);

        if (deliveryRef != null && notificationRef != null) {
            DeliveryService deliveryService = context.getService(deliveryRef);
            NotificationService notificationService = context.getService(notificationRef);

            String trackingId = "PKG123"; // Example tracking ID
            boolean delivered = deliveryService.isDelivered(trackingId);

            if (delivered) {
                System.out.println("Delivery Client: Package " + trackingId + " is delivered!");
                notificationService.sendNotification("Your package " + trackingId + " has arrived!");
            } else {
                System.out.println("Delivery Client: Package " + trackingId + " is still in transit.");
                notificationService.sendNotification("Your package " + trackingId + " is on the way.");
            }

            context.ungetService(deliveryRef);
            context.ungetService(notificationRef);
        } else {
            System.out.println("Delivery Client: One or more services not found.");
        }
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        System.out.println("Delivery Client: Stopping...");
    }
}