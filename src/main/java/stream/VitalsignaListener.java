
// rpm/stream/VitalSignListener.java
package rpm.stream;

import rpm.domain.VitalSign;

public interface VitalSignListener {
    void onVitalSignReceived(VitalSign vitalSign);
}