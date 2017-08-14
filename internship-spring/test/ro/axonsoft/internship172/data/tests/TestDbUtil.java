package ro.axonsoft.internship172.data.tests;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

public class TestDbUtil {

	public static final String T_USR = "USR";

	private final DataSource dataSource;

	public TestDbUtil(final DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setIdentity(final String tableName, final String columnIdName, final Integer identityNext) {
		try (final Connection connection = dataSource.getConnection()) {
			try (final Statement statement = connection.createStatement()) {
				statement.executeQuery(String.format("alter table %s alter column %s RESTART WITH %s", tableName,
						columnIdName, identityNext));
			}
		} catch (final SQLException e) {
			throw new IllegalStateException("Failed to set identity", e);
		}
	}

}
