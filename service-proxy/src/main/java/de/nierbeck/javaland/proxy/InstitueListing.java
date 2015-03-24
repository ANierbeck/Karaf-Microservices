package de.nierbeck.javaland.proxy;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class InstitueListing {

	@XmlElement
	public List<String> institutes;
	
}
