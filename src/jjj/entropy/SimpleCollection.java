package jjj.entropy;

import java.util.Collection;

public interface SimpleCollection<T>
{
	int Size();
	
	void AddAllTo(Collection<T> c);
	 
}
