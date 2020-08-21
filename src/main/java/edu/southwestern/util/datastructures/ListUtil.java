package edu.southwestern.util.datastructures;

import java.util.ArrayList;
import java.util.List;

public class ListUtil {

	/**
	 * Deep copies of list of list of elements of type T.
	 * @param <T> Type of element
	 * @param src Source list of lists
	 * @return deep copy of src
	 */
	public static <T> List<List<T>> deepCopyListOfLists(List<List<T>> src) {
		List<List<T>> dest = new ArrayList<List<T>>(src.size());
		for( List<T> sublist : src) {
		    dest.add(new ArrayList<>(sublist));
		}
		return dest;
	}
	
}
