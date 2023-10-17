import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class FramePortInfo {
    private String shortName;
    private String communicationDirection;

    public FramePortInfo(String shortName, String communicationDirection) {
        this.shortName = shortName;
        this.communicationDirection = communicationDirection;
    }

    public String getShortName() {
        return shortName;
    }

    public String getCommunicationDirection() {
        return communicationDirection;
    }
}

class CanFrameTriggeringInfo {
    private String shortName;
    private String framePortRef;

    public CanFrameTriggeringInfo(String shortName, String framePortRef) {
        this.shortName = shortName;
        this.framePortRef = framePortRef;
    }

    public String getShortName() {
        return shortName;
    }

    public String getFramePortRef() {
        return framePortRef;
    }
}

public class ARXMLParser {

    public static void main(String[] args) {

        String canClusterName = "enter_cluster_name_here";

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Get Document
            Document document = builder.parse(new File("sample.arxml"));

            // Normalize the xml structure
            document.getDocumentElement().normalize();

            List<FramePortInfo> framePortList = new ArrayList<>();
            NodeList framePortElements = document.getElementsByTagName("FRAME-PORT");
            for (int i = 0; i < framePortElements.getLength(); i++) {
                Element framePortElement = (Element) framePortElements.item(i);
                String shortName = framePortElement.getElementsByTagName("SHORT-NAME").item(0).getTextContent();
                String communicationDirection = framePortElement.getElementsByTagName("COMMUNICATION-DIRECTION").item(0).getTextContent();
                framePortList.add(new FramePortInfo(shortName, communicationDirection));
            }

            List<CanFrameTriggeringInfo> canFrameTriggeringList = new ArrayList<>();
            NodeList canClusterElements = document.getElementsByTagName("CAN-CLUSTER");
            for (int i = 0; i < canClusterElements.getLength(); i++) {
                Element canClusterElement = (Element) canClusterElements.item(i);
                String shortName = canClusterElement.getElementsByTagName("SHORT-NAME").item(0).getTextContent();
                if (shortName.equals(canClusterName)) {
                    NodeList canFrameTriggeringElements = canClusterElement.getElementsByTagName("CAN-FRAME-TRIGGERING");
                    for (int j = 0; j < canFrameTriggeringElements.getLength(); j++) {
                        Element canFrameTriggeringElement = (Element) canFrameTriggeringElements.item(j);
                        String frameShortName = canFrameTriggeringElement.getElementsByTagName("SHORT-NAME").item(0).getTextContent();
                        String framePortRefs = canFrameTriggeringElement.getElementsByTagName("FRAME-PORT-REFS").item(0).getTextContent();
                        // Extract the text behind the last "/" in framePortRefs.
                        int lastSlashIndex = framePortRefs.lastIndexOf("/");
                        if (lastSlashIndex >= 0) {
                            framePortRefs = framePortRefs.substring(lastSlashIndex + 1);
                        }
                        canFrameTriggeringList.add(new CanFrameTriggeringInfo(frameShortName, framePortRefs));
                    }
                }
            }

            System.out.println("\nCAN-FRAME-TRIGGERING Elements for " + canClusterName + ":");
            for (CanFrameTriggeringInfo entry : canFrameTriggeringList) {
                System.out.println("\nShort Name: " + entry.getShortName());
                for (FramePortInfo framePort : framePortList) {
                    if (framePort.getShortName().trim().equals(entry.getFramePortRef().trim())) {
                        System.out.println("Communication Direction: " + framePort.getCommunicationDirection());
                        break;
                    }
                }
            }

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

}
