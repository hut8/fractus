package fractus.main;

import java.util.HashSet;

public class Delegate<T extends DelegateMethod<TData>, TData extends EventData> {
	private HashSet<T> targets;

	public Delegate() {
		targets = new HashSet<T>();
	}
	
	public synchronized void addTarget(T target) {
		targets.add(target);
	}
	
	public synchronized void removeTarget(T target) {
		targets.remove(target);
	}
	
	public synchronized void invoke(TData data) {
		for (T l : targets) {
			l.invoke(data);
		}
	}
}
