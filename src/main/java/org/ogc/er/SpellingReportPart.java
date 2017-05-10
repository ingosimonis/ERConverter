package org.ogc.er;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;

/**
 * ReportPart is single file part of a Spelling Report. It contains the file name as header and a 
 * Multimap<String line number, String listOfErrors
 * @author isi
 *
 */
public class SpellingReportPart extends ReportPart {
	Multimap<Integer,String> content;
	
	public SpellingReportPart(String header) {
		super(header);
	}
	
	public SpellingReportPart(String header, Multimap<Integer,String> content) {
		super(header);
		this.content = content;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
//		Multimap<String, String> sorted = this.sortedByDescendingFrequency(content);
		Map<Integer, String> sorted = this.sort(content);
		StringBuffer buf = new StringBuffer();
		buf.append("\n");
		buf.append(header);
		buf.append("\n");
		
		Set<Integer> keys = sorted.keySet();
		for(Integer s : keys) {
			buf.append("Line: ");
			if(s.intValue() <10) {
				buf.append("  ");
				buf.append(s);
			} else if(s.intValue() <100) {
				buf.append(" ");
				buf.append(s);
			} else {
				buf.append(s);
			}
		    buf.append(" ");
		    buf.append(sorted.get(s));
		    buf.append("\n");
		}
		return buf.toString();
	}
	
	/**
	 * quick and dirty sort
	 * @param multimap
	 * @return
	 */
	private Map<Integer, String> sort(Multimap<Integer, String> multimap) {
		Map<Integer, String> ascSortedMap = new TreeMap<Integer, String>();
		Collection<String> values;
		for(Integer key : multimap.keySet()) {
			values = multimap.get(key);
			ascSortedMap.put(key, values.toString());
		}
		return ascSortedMap;
	}
	

	// approach to sort my number of times a given value exists in the map
	/**
	 * @return a {@link Multimap} whose entries are sorted by descending frequency
	 */
	public Multimap<String, String> sortedByDescendingFrequency(Multimap<String, String> multimap) {
	    // ImmutableMultimap.Builder preserves key/value order
	    ImmutableMultimap.Builder<String, String> result = ImmutableMultimap.builder();
	    for (Multiset.Entry<String> entry : DESCENDING_COUNT_ORDERING.sortedCopy(multimap.keys().entrySet())) {
	        result.putAll(entry.getElement(), multimap.get(entry.getElement()));
	    }
	    return result.build();
	}

	/**
	 * An {@link Ordering} that orders {@link Multiset.Entry Multiset entries} by ascending count.
	 */
	private static final Ordering<Multiset.Entry<?>> ASCENDING_COUNT_ORDERING = new Ordering<Multiset.Entry<?>>() {
	    @Override
	    public int compare(Multiset.Entry<?> left, Multiset.Entry<?> right) {
	        return Ints.compare(left.getCount(), right.getCount());
	    }
	};

	/**
	 * An {@link Ordering} that orders {@link Multiset.Entry Multiset entries} by descending count.
	 */
	private static final Ordering<Multiset.Entry<?>> DESCENDING_COUNT_ORDERING = ASCENDING_COUNT_ORDERING.reverse();
	
}
