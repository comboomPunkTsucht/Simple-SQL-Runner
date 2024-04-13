package components;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.table.AbstractTableModel;

//Custom TableModel class
public class ResultSetTableModel extends AbstractTableModel {
 private ResultSet resultSet;

 public ResultSetTableModel(ResultSet resultSet) {
     this.resultSet = resultSet;
 }

 @Override
 public int getRowCount() {
     try {
         resultSet.last();
         return resultSet.getRow();
     } catch (SQLException e) {
         e.printStackTrace();
         return 0;
     }
 }

 @Override
 public int getColumnCount() {
     try {
         return resultSet.getMetaData().getColumnCount();
     } catch (SQLException e) {
         e.printStackTrace();
         return 0;
     }
 }

 @Override
 public Object getValueAt(int rowIndex, int columnIndex) {
     try {
         resultSet.absolute(rowIndex + 1);
         return resultSet.getObject(columnIndex + 1);
     } catch (SQLException e) {
         e.printStackTrace();
         return null;
     }
 }

 @Override
 public String getColumnName(int column) {
     try {
         return resultSet.getMetaData().getColumnName(column + 1);
     } catch (SQLException e) {
         e.printStackTrace();
         return "";
     }
 }
}