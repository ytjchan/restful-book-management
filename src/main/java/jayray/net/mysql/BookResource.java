package jayray.net.mysql;

import java.util.List;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.naming.NotContextException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

//import com.mysql.fabric.Response;
import com.sun.jersey.api.NotFoundException;

@Path("books")
public class BookResource {
	public static String DB_URI = "jdbc:mysql://localhost:3306/bookmanagement";
	public static String DB_USER = "root";
	public static String DB_PASS = "root";

	@POST
	//@Path("here")
	@Consumes({ MediaType.APPLICATION_JSON })
	public Response addNewBook(Book book) {
		try {
            // The newInstance() call is a work around for some
            // broken Java implementations
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            // handle the error
        		throw new IllegalStateException("Driver not found!", ex);
        }
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(DB_URI, DB_USER, DB_PASS);
			Statement stmt = conn.createStatement();
			
			// SQL query formation
			String sql1 = "SELECT * FROM book WHERE title=\"" + book.getTitle() + "\" AND author=\"" + book.getAuthor()
					+ "\" AND publisher=\"" + book.getPublisher() + "\" AND year=" + book.getYear();
			String sql2 = "INSERT INTO book (title, author, publisher, year) values("
					+ "\"" + book.getTitle() + "\" , \"" + book.getAuthor() + "\" , \"" + book.getPublisher() + "\" , " + book.getYear() + " )";
			
			// Generating and returning Book objects
			ResultSet rs = stmt.executeQuery(sql1);
			if (rs.next())
				return Response.status(409).entity("Duplicate record: /books/"+rs.getInt("id"))
						.build();
			stmt.executeUpdate(sql2);
			ResultSet rs2 = stmt.executeQuery(sql1);
			boolean found = rs2.next();
			assert found;
			int id = rs2.getInt("id");
			return Response.status(201).entity("Location: /books/"+id+"\nVisit http://localhost:8080/BookManagementService/books/"+id+" to view the record")
					.build();
		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			throw new IllegalStateException("Cannot connect the database!", ex);
		}
	}
	
	
	@GET
	//@Path("here")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getAllBooks(
			@QueryParam("id") Integer id, 
			@QueryParam("title") String title, 
			@QueryParam("author") String author, 
			@QueryParam("publisher") String publisher, 
			@QueryParam("limit") Integer limit, 
			@QueryParam("sortby") String sortBy,
			@QueryParam("order") String order) {
		List<Book> books = new ArrayList<Book>();
		try {
            // The newInstance() call is a work around for some
            // broken Java implementations

            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            // handle the error
        		throw new IllegalStateException("Driver not found!", ex);
        }
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(DB_URI, DB_USER, DB_PASS);
			Statement stmt = conn.createStatement();
			
			// SQL query formation
			StringBuilder sql = new StringBuilder("SELECT * FROM book ");
			if (id != null || title != null || author != null || publisher != null) {
				sql.append("WHERE ");
				if (id != null)
					sql.append("id = \""+id+"\" AND ");
				if (title != null)
					sql.append("title LIKE \"%"+title+"%\" AND ");
				if (author != null)
					sql.append("author LIKE \"%"+author+"%\" AND ");
				if (publisher != null)
					sql.append("publisher LIKE \"%"+publisher+"%\" AND ");
				sql.delete(sql.length()-4, sql.length()-1);
				sql.append(' ');
			}
			if (sortBy != null) {
				sql.append("ORDER BY " + sortBy + " ");
				if (order != null) {
					if (order.equals("asc"))
						sql.append("ASC ");
					else if (order.equals("desc"))
						sql.append("DESC ");
				}
			}
			// Generating and returning Book objects
			ResultSet rs = stmt.executeQuery(sql.toString());
			while (rs.next()) {
				int bookId = rs.getInt("id");
				String bookTitle = rs.getString("title");
				String bookAuthor = rs.getString("author");
				String bookPublisher = rs.getString("publisher");
				int bookYear = rs.getInt("year");
				boolean bookAvailable = rs.getBoolean("available");
				Book b = new Book();
				b.setId(bookId);
				b.setTitle(bookTitle);
				b.setAuthor(bookAuthor);
				b.setPublisher(bookPublisher);
				b.setYear(bookYear);
				b.setAvailable(bookAvailable);
				books.add(b);
				if (limit != null)
					if (--limit<=0)
						break;
			}
			// 204 No Content must not have payload, or else treated as 200
			if (books.size()==0)
				return Response.status(204)
						.build();
			// Just put the JSON object into entity and it will returned with the HTTP status
			return Response.ok()
					.entity(new BookQuery(books.size(), books))
					.build();

		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			throw new IllegalStateException("Cannot connect the database!", ex);
		}
	}

	@GET
	@Path("{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getBook(@PathParam("id") int id) {
		try {
            // The newInstance() call is a work around for some
            // broken Java implementations
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            // handle the error
        		throw new IllegalStateException("Driver not found!", ex);
        }
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(DB_URI, DB_USER, DB_PASS);
			Statement stmt = conn.createStatement();
			
			// SQL query formation
			String sql = "SELECT * FROM book WHERE id="+id;
			// Generating and returning Book objects
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				int bookId = rs.getInt("id");
				String bookTitle = rs.getString("title");
				String bookAuthor = rs.getString("author");
				String bookPublisher = rs.getString("publisher");
				int bookYear = rs.getInt("year");
				boolean bookAvailable = rs.getBoolean("available");
				Book book = new Book(bookId, bookTitle, bookAuthor, bookPublisher, bookYear, bookAvailable);
				// Just put the JSON object into entity and it will returned with the HTTP status
				return Response.status(200).entity(book).build();
			} else {
				return Response.status(404).entity("No book record").build();
			}
		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			throw new IllegalStateException("Cannot connect the database!", ex);
		}
	}
	
	@PUT
	@Path("{id}")
	@Consumes({ MediaType.APPLICATION_JSON })
	public Response loanOrReturnBook(Available available, @PathParam("id") int id) {
		boolean target = available.isAvailable();
		try {
            // The newInstance() call is a work around for some
            // broken Java implementations
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            // handle the error
        	throw new IllegalStateException("Driver not found!", ex);
        }
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(DB_URI, DB_USER, DB_PASS);
			Statement stmt = conn.createStatement();
			
			// SQL query formation
			String sql1 = "SELECT available FROM book WHERE id="+id;
			String sql2 = "UPDATE book SET available=" + target + " WHERE id=" + id;
			
			// See if the book exists
			ResultSet rs = stmt.executeQuery(sql1);
			if (rs.next()) {
				// Generating and returning Book objects
				boolean current = rs.getBoolean("available");
				boolean loanStatus = false;
				boolean returnStatus = false;
				if (target==false && current==true) // Successful loan
					loanStatus = true;
				else if (target==true && current==false) // Successful return
					returnStatus = true;
				if (loanStatus==true || returnStatus==true) {
					stmt.executeUpdate(sql2);
					return Response.ok().entity("OK").build();
				} else {
					return Response.status(400).entity("Bad request - Book already "+((current)?"returned":"loaned")).build();
				}
			} else {
				return Response.status(404).entity("No book record").build();
			}
			
		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			throw new IllegalStateException("Cannot connect the database!", ex);
		}
		
	}
	
	@DELETE
	@Path("{id}")
	public Response deleteBook(@PathParam("id") int id) {
		try {
            // The newInstance() call is a work around for some
            // broken Java implementations
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            // handle the error
        		throw new IllegalStateException("Driver not found!", ex);
        }
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(DB_URI, DB_USER, DB_PASS);
			Statement stmt = conn.createStatement();
			
			// SQL query formation
			String sql1 = "SELECT * FROM book WHERE id="+id;
			String sql2 = "DELETE FROM book WHERE id="+id;
			
			// See if the book exists
			ResultSet rs = stmt.executeQuery(sql1);
			if (rs.next()) {
				// Generating and returning Book objects
				stmt.executeUpdate(sql2);
				return Response.ok().entity("OK").build();
			} else {
				return Response.status(404).entity("No book record").build();
			}
			
		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			throw new IllegalStateException("Cannot connect the database!", ex);
		}
	}
	
}
