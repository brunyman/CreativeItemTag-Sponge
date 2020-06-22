package net.craftersland.sponge.creativetag.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import org.spongepowered.api.scheduler.Task;

import net.craftersland.sponge.creativetag.CT;

public class MysqlSetup {
	
	private CT pl;
	private Connection conn = null;
	
	public MysqlSetup(CT plugin) {
		this.pl = plugin;
		setupDatabase();
		//databaseMaintenanceTask();
	}
	
	public Connection getConnection() {
		checkConnection();
		return conn;
	}
	
	private void checkConnection() {
		try {
			if (conn == null) {
				CT.log.warn("Database connection failed. Reconnecting...");
				conn = null;
				connectToDatabase();
			} else if (!conn.isValid(3)) {
				CT.log.warn("Database connection failed. Reconnecting...");
				conn = null;
				connectToDatabase();
			} else if (conn.isClosed() == true) {
				CT.log.warn("Database connection failed. Reconnecting...");
				conn = null;
				connectToDatabase();
			}
		} catch (Exception e) {
			CT.log.error("Error re-connecting to the database! Error: " + e.getMessage());
		}
	}
	
	public void closeConnection() {
		try {
			CT.log.info("Closing database connection...");
			if (conn != null) {
				conn.close();
				conn = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void setupDatabase() {
		Task.builder().async().execute(new Runnable() {

			@Override
			public void run() {
				connectToDatabase();
				if (conn != null) {
					setupTables();
				}
			}
			
		}).submit(pl);
	}
	
	public void setupTables() {
		PreparedStatement query1 = null;
		try {
			String data = "CREATE TABLE IF NOT EXISTS `" + pl.getConfigHandler().getString("Database", "Mysql", "TableName") + "` (id INT UNSIGNED NOT NULL AUTO_INCREMENT, chunk varchar(20) UNIQUE NOT NULL, blocks TEXT NOT NULL, last_updated char(13) NOT NULL, PRIMARY KEY(id));";
	        query1 = conn.prepareStatement(data);
	        query1.execute();
		} catch (Exception e) {
			CT.log.error("Error creating tables! Error: " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (query1 != null) {
					query1.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void connectToDatabase() {
		try {
       	 	//Load Drivers
            Class.forName("com.mysql.jdbc.Driver");
            Properties properties = new Properties();
            properties.setProperty("user", pl.getConfigHandler().getString("Database", "Mysql", "User"));
            properties.setProperty("password", pl.getConfigHandler().getString("Database", "Mysql", "Password"));
            properties.setProperty("autoReconnect", "true");
            properties.setProperty("verifyServerCertificate", "false");
            properties.setProperty("useSSL", pl.getConfigHandler().getString("Database", "Mysql", "SSL"));
            properties.setProperty("requireSSL", pl.getConfigHandler().getString("Database", "Mysql", "SSL"));
            
            //Connect to database
            conn = DriverManager.getConnection("jdbc:mysql://" + pl.getConfigHandler().getString("Database", "Mysql", "Host") + ":" + pl.getConfigHandler().getString("Database", "Mysql", "Port") + "/" + pl.getConfigHandler().getString("Database", "Mysql", "DatabaseName") + "?", properties);
            CT.log.info("Database connection established!");
          } catch (ClassNotFoundException e) {
        	  CT.log.error("Could not locate drivers for mysql! Error: " + e.getMessage());
          } catch (SQLException e) {
        	  CT.log.error("Could not connect to mysql database! Error: " + e.getMessage());
          } catch (Exception ex) {
        	  ex.printStackTrace();
          }
	}
	
	/*private void databaseMaintenanceTask() {
		if (plugin.getConfigHandler().getBoolean("Database.RemoveInactiveUsers.enabled") == true) {
			Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {

				@Override
				public void run() {
					if (conn != null) {
						long inactivityDays = Long.parseLong(plugin.getConfigHandler().getString("Database.RemoveInactiveUsers.inactivity"));
						long inactivityMils = inactivityDays * 24 * 60 * 60 * 1000;
						long curentTime = System.currentTimeMillis();
						long inactiveTime = curentTime - inactivityMils;
						RR.log.info("Database maintenance task started...");
						tableMaintenance(inactiveTime, getConnection(), plugin.getConfigHandler().getString("Database.Mysql.TableName"));
						RR.log.info("Database maintenance complete!");
					}
				}
				
			}, 100 * 20L);
		}
	}
	
	private void tableMaintenance(long inactiveTime, Connection conn, String tableName) {
		PreparedStatement preparedStatement = null;
		try {
			String sql = "DELETE FROM `" + tableName + "` WHERE `last_seen` < ?";
			preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, String.valueOf(inactiveTime));
			preparedStatement.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}*/

}
