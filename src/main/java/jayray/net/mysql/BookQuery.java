package jayray.net.mysql;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BookQuery {
	
	private int foundBooks=0;
	private List<Book> results = new ArrayList<Book>();
	
	@XmlElement(name="FoundBooks")
	public int getFoundBooks() {
		return foundBooks;
	}
	public void setFoundBooks(int foundBooks) {
		this.foundBooks = foundBooks;
	}
	
	@XmlElement(name="Results")
	public List<Book> getResults() {
		return results;
	}
	public void setResults(List<Book> results) {
		this.results = results;
	}
	
	public BookQuery() {
		super();
	}
	
	public BookQuery(int foundBooks, List<Book> results) {
		super();
		this.foundBooks = foundBooks;
		this.results = results;
	}
	
	
}
