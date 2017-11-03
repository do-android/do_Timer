package doext.implement;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import android.app.Activity;
import core.DoServiceContainer;
import core.helper.DoTextHelper;
import core.interfaces.DoIScriptEngine;
import core.object.DoInvokeResult;
import doext.define.do_Timer_IMethod;
import doext.define.do_Timer_MAbstract;

/**
 * 自定义扩展MM组件Model实现，继承do_Timer_MAbstract抽象类，并实现do_Timer_IMethod接口方法；
 * #如何调用组件自定义事件？可以通过如下方法触发事件：
 * this.model.getEventCenter().fireEvent(_messageName, jsonResult);
 * 参数解释：@_messageName字符串事件名称，@jsonResult传递事件参数对象；
 * 获取DoInvokeResult对象方式new DoInvokeResult(this.getUniqueKey());
 */
public class do_Timer_Model extends do_Timer_MAbstract implements do_Timer_IMethod{

	private boolean isStop = false;
	private Timer timer;
	private TimerTask task;
	
	public do_Timer_Model() throws Exception {
		super();
		timer = new Timer();
	}
	
	/**
	 * 同步方法，JS脚本调用该组件对象方法时会被调用，可以根据_methodName调用相应的接口实现方法；
	 * @_methodName 方法名称
	 * @_dictParas 参数（K,V）
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_invokeResult 用于返回方法结果对象
	 */
	@Override
	public boolean invokeSyncMethod(String _methodName, JSONObject _dictParas,
			DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		if("start".equals(_methodName)){
			start(_dictParas, _scriptEngine, _invokeResult);
			return true;
		}
		if("stop".equals(_methodName)){
			stop(_dictParas, _scriptEngine, _invokeResult);
			return true;
		}
		if("isStart".equals(_methodName)){
			isStart(_dictParas, _scriptEngine, _invokeResult);
			return true;
		}
		return super.invokeSyncMethod(_methodName, _dictParas, _scriptEngine, _invokeResult);
	}
	
	/**
	 * 异步方法（通常都处理些耗时操作，避免UI线程阻塞），JS脚本调用该组件对象方法时会被调用，
	 * 可以根据_methodName调用相应的接口实现方法；
	 * @_methodName 方法名称
	 * @_dictParas 参数（K,V）
	 * @_scriptEngine 当前page JS上下文环境
	 * @_callbackFuncName 回调函数名
	 * #如何执行异步方法回调？可以通过如下方法：
	 * _scriptEngine.callback(_callbackFuncName, _invokeResult);
	 * 参数解释：@_callbackFuncName回调函数名，@_invokeResult传递回调函数参数对象；
	 * 获取DoInvokeResult对象方式new DoInvokeResult(this.getUniqueKey());
	 */
	@Override
	public boolean invokeAsyncMethod(String _methodName, JSONObject _dictParas,
			DoIScriptEngine _scriptEngine, String _callbackFuncName) throws Exception{
		return super.invokeAsyncMethod(_methodName, _dictParas, _scriptEngine, _callbackFuncName);
	}

	/**
	 * 启动；
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_invokeResult 用于返回方法结果对象
	 */
	@Override
	public void start(JSONObject _dictParas, DoIScriptEngine _scriptEngine,
			final DoInvokeResult _invokeResult) throws Exception {
		long delay = DoTextHelper.strToLong(getPropertyValue("delay"), 0);
		long period = DoTextHelper.strToLong(getPropertyValue("interval"), 1000);
		cancelTimerTask();
	    if(task == null){
	    	isStop = false;
	    	task = new TimerTask(){
				@Override
				public void run() {
					Activity activity = (Activity)DoServiceContainer.getPageViewFactory().getAppContext();
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if(!isStop && getEventCenter() != null) {
								getEventCenter().fireEvent("tick", _invokeResult);
							}
						}
					});
				}
		    };
		    try{
		    	timer.schedule(task, delay, period);
		    }catch(IllegalArgumentException e){
		    	DoServiceContainer.getLogEngine().writeInfo("定时器启动失败：delay不能小于0且interval不能小于等于0", "do_Timer");
		    }
	    }
	}

	/**
	 * 取消；
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_invokeResult 用于返回方法结果对象
	 */
	@Override
	public void stop(JSONObject _dictParas, DoIScriptEngine _scriptEngine,
			DoInvokeResult _invokeResult) throws Exception {
		cancelTimerTask();
	}

	@Override
	public void dispose() {
		super.dispose();
		cancelTimerTask();
		timer.cancel();
		timer = null;
	}

	@Override
	public void isStart(JSONObject _dictParas, DoIScriptEngine _scriptEngine,
			DoInvokeResult _invokeResult) throws Exception {
		_invokeResult.setResultBoolean(null != task && !isStop ? true : false);
	}
	
	private void cancelTimerTask(){
		if(!isStop){
			isStop = true;
		}
		if(task != null){
			task.cancel();
			task = null;
		}
	}
}