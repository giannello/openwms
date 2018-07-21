package org.openhab.binding.openwms.messages;

import static org.openhab.binding.openwms.config.OpenWMSBindingConstants.*;

import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.OpenClosedType;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.library.types.StopMoveType;
import org.eclipse.smarthome.core.library.types.UpDownType;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.Type;
import org.openhab.binding.openwms.internal.OpenWMSDeviceConfiguration;

/*
* @author zeezee - Initial contribution
*/
public class OpenWMSGetResponse {

    public String paketTyp;
    public String msgTyp; // 8011
    // public String panId;
    public String deviceId;
    public String position;
    public String angle;
    public String valance1;
    public String valance2;
    public String panId;
    public String deviceTyp;

    public Commands command;

    public enum Commands {
        OFF(0),
        ON(1),
        OPEN(0),
        CLOSE(1),
        STOP(2),
        CHANGE_DIRECTON(7);

        private final int command;

        Commands(int command) {
            this.command = command;
        }

    }

    public OpenWMSGetResponse(String data) {
        // noch sind die Daten in raw-Form ->
        // Antwort Status {rE49D0880110100000520FFFFFF00}
        // Antwort Fahrbefehl {rE49D0870710010023F023EFFFFFF0CFFFFFF}
        // Scan {rE18F0670204DE402}
        // Antwort Scan {rE49D0870214DE4258C2F000300000000000000000304000100C1000000000000}

        setPaketTyp(data);
        String payload = "0";
        if (paketTyp.equals("r")) {
            setDeviceId(data.substring(2, 8));
            setMsgTyp(data.substring(8, 12));
            switch (msgTyp) {
                case "7020":
                    payload = data.substring(12);
                    setPanId(data);
                    setDeviceTyp(data);
                    break;
                case "7021":
                    payload = data.substring(12);
                    setPanId(data);
                    setDeviceTyp(data);
                    break;
                case "8011":
                    switch (data.substring(12, 20)) {
                        case "01000003": // position
                            // TODO
                            payload = data.substring(20);
                            setPosition(String.valueOf(Integer.parseInt(payload.substring(0, 2), 16) / 2));
                            break;
                        case "01000005": // position
                            // TODO Valance, Angle etc.
                            payload = data.substring(20);
                            setPosition(String.valueOf(Integer.parseInt(payload.substring(0, 2), 16) / 2));
                            break;
                        case "26000046":
                            // TODO
                            break;
                        case "0C000006":
                            // TODO
                            break;
                    }

                    break;
            }

        }
    }

    public String getPaketTyp() {
        return paketTyp;
    }

    public void setPaketTyp(String paketTyp) {
        String paket = paketTyp.replaceAll("[{}]", "");
        paket = paket.substring(0, 1);
        this.paketTyp = paket;
    }

    public void setMsgTyp(String msgTyp) {
        this.msgTyp = msgTyp;
    }

    public String getMsgTyp() {
        return msgTyp;
    }

    public String getPosition() {
        return position;
    }

    public void setDeviceTyp(String deviceTyp) {
        String paket = deviceTyp.replaceAll("[{}]", "");
        paket = paket.substring(15, 17);
        this.deviceTyp = paket;
    }

    public String getDeviceTyp() {
        return deviceTyp;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setPanId(String panId) {
        String paket = panId.replaceAll("[{}]", "");
        paket = paket.substring(11, 15);
        this.panId = paket;
    }

    public String getPanId() {
        return panId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getAngle() {
        return angle;
    }

    public void setAngle(String angle) {
        this.angle = angle;
    }

    public String getValance1() {
        return valance1;
    }

    public void setValance1(String valance1) {
        this.valance1 = valance1;
    }

    public String getValance2() {
        return valance2;
    }

    public void setValance2(String valance2) {
        this.valance2 = valance2;
    }

    public State convertToState(String channelId) {
        switch (channelId) {
            case CHANNEL_DIMMINGLEVEL:
                return new PercentType(position);

            case CHANNEL_COMMAND:
                int wert = Integer.valueOf(position);
                if (wert == 0) {
                    return OnOffType.OFF;
                } else {
                    return OnOffType.ON;
                }
                // return (command == Commands.OFF ? OnOffType.OFF : OnOffType.ON);

            case CHANNEL_SHUTTER:
                return new PercentType(position);

            default:
                return new DecimalType(position);
            // return super.convertToState(channelId);
        }
    }

    public void convertFromState(String channelId, Type type) {
        if (CHANNEL_SHUTTER.equals(channelId)) {
            if (type instanceof OpenClosedType) {
                command = (type == OpenClosedType.CLOSED ? Commands.CLOSE : Commands.OPEN);
            } else if (type instanceof UpDownType) {
                command = (type == UpDownType.UP ? Commands.OPEN : Commands.CLOSE);
            } else if (type instanceof StopMoveType) {
                command = Commands.STOP;
            }
        }

    }

    public void setConfig(OpenWMSDeviceConfiguration deviceConfiguration) {
        // TODO Auto-generated method stub

    }

    public void encodeMessage(byte[] data) {
        // TODO Auto-generated method stub

    }

    public byte[] decodeMessage() {
        // TODO Auto-generated method stub
        return null;
    }

    public void addDevicePropertiesTo(DiscoveryResultBuilder discoveryResultBuilder) {
        // super.addDevicePropertiesTo(discoveryResultBuilder);
        discoveryResultBuilder.withProperty(PROPERTY_DEVICEID, deviceId);
        discoveryResultBuilder.withProperty(PROPERTY_PANID, panId);

    }

}
