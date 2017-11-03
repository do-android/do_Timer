package dotest.module.activity;

import android.view.View;
import core.DoServiceContainer;
import doext.implement.do_Timer_Model;
import dotest.module.frame.debug.DoService;

public class TimerTestActivity extends DoTestActivity{

	@Override
	protected void initModuleModel() throws Exception {
		super.initModuleModel();
		this.model = new do_Timer_Model();
	}


	@Override
	protected void doTestSyncMethod() {
        DoService.syncMethod(this.model, "start", null);
	}

	@Override
	protected void onEvent() {
		DoService.subscribeEvent(this.model, "tick", new DoService.EventCallBack() {
			@Override
			public void eventCallBack(String _data) {
				DoServiceContainer.getLogEngine().writeDebug("事件回调：" + _data);
			}
		});
	}

	@Override
	public void doTestFireEvent(View view) {
		// TODO Auto-generated method stub
		super.doTestFireEvent(view);
	}
	
	

}
