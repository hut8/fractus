package fractus.delegate;


public interface DelegateMethod<TData extends EventData> {
	public void invoke(TData delegateData);
}
