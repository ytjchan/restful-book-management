package jayray.net.mysql;

import java.util.List;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("user")
public class UserResource {
	public static String DB_URI = "jdbc:mysql://localhost/RestDemo";
	public static String DB_USER = "root";
	public static String DB_PASS = "";

	@GET
	@Path("lalala")
	@Produces({ MediaType.APPLICATION_JSON })
	public List<User> getAllUsers() {
		List<User> users = new ArrayList<User>();
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
			String sql;
			sql = "SELECT * FROM t_user";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String id = rs.getString("userId");
				String name = rs.getString("userName");
				String age = rs.getString("age");
				User u = new User();
				u.setId(id);
				u.setName(name);
				u.setAge(age);
				users.add(u);
			}
		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			throw new IllegalStateException("Cannot connect the database!", ex);
		}

		return users;
	}

	// @GET
	// @Path("id/{id}")
	// @Produces({ MediaType.APPLICATION_JSON })
	// public User getUser(@PathParam("id") String id) {
	// CustomerDao customerDao = new CustomerDao();
	// return customerDao.fetchCustomer(id);
	// }
}
