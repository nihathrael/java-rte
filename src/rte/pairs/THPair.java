package rte.pairs;

import org.w3c.dom.Element;

public class THPair implements Comparable<THPair> {

	public int id;
	public String task;
	public boolean entailment;

	public Text text;
	public Text hypothesis;

	public static THPair fromXML(Element pairElement) {
		THPair pair = new THPair();
		pair.id = Integer.parseInt(pairElement.getAttribute("id"));
		pair.task = pairElement.getAttribute("task");
		pair.entailment = pairElement.getAttribute("entailment").equals("YES");
		
		Element text = (Element)pairElement.getElementsByTagName("text").item(0);
		pair.text = Text.fromXML(text);
		
		Element hypothesis = (Element)pairElement.getElementsByTagName("hypothesis").item(0);
		pair.hypothesis = Text.fromXML(hypothesis);
		

		return pair;
	}
	
	public int compareTo(THPair o) {
		return new Integer(id).compareTo(o.id);
	}

}
