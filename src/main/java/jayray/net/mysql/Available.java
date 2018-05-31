package jayray.net.mysql;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Available {
	
	private boolean available;

	@XmlElement(name="Available")
	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public Available() {
		super();
	}

	public Available(boolean available) {
		super();
		this.available = available;
	}

}
