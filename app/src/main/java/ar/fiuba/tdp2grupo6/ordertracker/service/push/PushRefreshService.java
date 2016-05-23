package ar.fiuba.tdp2grupo6.ordertracker.service.push;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import ar.fiuba.tdp2grupo6.ordertracker.business.PushBZ;
import ar.fiuba.tdp2grupo6.ordertracker.dataaccess.SharedPrefDA;

public class PushRefreshService extends FirebaseInstanceIdService {

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is also called
     * when the InstanceID token is initially generated, so this is where
     * you retrieve the token.
     */
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("LOG", "Refreshed token: " + refreshedToken);

        try {
            PushBZ pushBZ = new PushBZ(this);
            pushBZ.enviarPushToken(refreshedToken);
        } catch (Exception e) {

        }
   }

}