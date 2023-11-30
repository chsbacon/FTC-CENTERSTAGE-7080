package org.firstinspires.ftc.teamcode.modules;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.canvas.Canvas;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;

public class ActionExecutor {
    private Action action = null;

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public boolean actionIsActive(){
        return action != null;
    }

    public void doLoop(){
        if(action != null){
            FtcDashboard dash = FtcDashboard.getInstance();
            TelemetryPacket packet = new TelemetryPacket();
            Canvas canvas = new Canvas();
            action.preview(canvas);
            if (action.run(packet)) {
                packet.fieldOverlay().getOperations().addAll(canvas.getOperations());
            } else {
                action = null;
            }
            dash.sendTelemetryPacket(packet);
        }
    }
}
