package stream;

import AllVitalSigns.VitalSign;

public interface VitalSignListener {
    void onVitalSignReceived(VitalSign vitalSign);
}