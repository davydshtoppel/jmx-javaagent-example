package com.oracle.coherence.examples.todo;

import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.util.*;
import java.util.stream.Collectors;

public class JmxTest {

    public static void main(String[] args) throws Exception {
        System.out.println("Create an RMI connector client and connect it to the RMI connector server");
        JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://:9000/jmxrmi");
        try (JMXConnector jmxc = JMXConnectorFactory.connect(url, null)) {
            MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
//            mbsc.addNotificationListener(null, new ClientListener(), null, null);

            Set<ObjectName> names = new TreeSet<>(mbsc.queryNames(new ObjectName("Coherence:*"), null));
            for (ObjectName name : names) {
                System.out.println("ObjectName = " + name);
                MBeanInfo info = mbsc.getMBeanInfo(name);
                MBeanAttributeInfo[] attributes = info.getAttributes();
                String[] attrNames = Arrays.stream(attributes)
                        .map(MBeanFeatureInfo::getName)
                        .toArray(String[]::new);

                AttributeList attributeList = mbsc.getAttributes(name, attrNames);
                for (Object attr : attributeList) {
                    Attribute attribute = (Attribute) attr;
                    MBeanAttributeInfo attributeInfo = Arrays.stream(attributes)
                            .filter(it -> Objects.equals(it.getName(), attribute.getName()))
                            .findAny()
                            .orElseThrow();

                    System.out.println("\t Attribute: name [" + attributeInfo.getName()
                            + "], type [" + attributeInfo.getType()
//                            + "], description [" + attributeInfo.getDescription()
                            + "], value [" + attribute.getValue() + "]");
                }
            }
        }
    }

    public static class ClientListener implements NotificationListener {

        public void handleNotification(Notification notification,
                                       Object handback) {
            System.out.println("Received notification:");
            System.out.print("\tClassName: " + notification.getClass().getName());
            System.out.print("\tSource: " + notification.getSource());
            System.out.print("\tType: " + notification.getType());
            System.out.print("\tMessage: " + notification.getMessage());
            if (notification instanceof AttributeChangeNotification) {
                AttributeChangeNotification acn =
                        (AttributeChangeNotification) notification;
                System.out.print("\tAttributeName: " + acn.getAttributeName());
                System.out.print("\tAttributeType: " + acn.getAttributeType());
                System.out.print("\tNewValue: " + acn.getNewValue());
                System.out.print("\tOldValue: " + acn.getOldValue());
            }
        }
    }

}
