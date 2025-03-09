package com.example.osgi.producer.delivery;

public class DeliveryServiceImpl implements DeliveryService, BundleActivator {
    private ServiceRegistration<DeliveryService> serviceRegistration;
    private final Set<String> deliveredPackages = new HashSet<>();

    @Override
    public void start(BundleContext context) throws Exception {
        System.out.println("Delivery Service: Starting...");

        deliveredPackages.add("PKG123");
        deliveredPackages.add("PKG456");

        serviceRegistration = context.registerService(DeliveryService.class, this, null);
        System.out.println("Delivery Service: Registered.");
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        System.out.println("Delivery Service: Stopping...");

        if (serviceRegistration != null) {
            serviceRegistration.unregister();
            System.out.println("Delivery Service: Unregistered.");
        }
    }

    @Override
    public boolean isDelivered(String trackingId) {
        return deliveredPackages.contains(trackingId);
    }
}