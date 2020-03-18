package todo.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import todo.dto.Todo;

public class TodoDAO extends DAO{
	public List<Todo> todoList() throws Exception{
		List<Todo> returnList = new ArrayList<Todo>();

		String sql = "SELECT id , title , task , limitdate , lastupdate , userid , label , td.status , filename " +
					"FROM todo_list td LEFT JOIN status_list stts ON stts.status = td.status";


		//	プリペアステートメントを取得し、実行SQLを渡す
		PreparedStatement statement = getPreparedStatement(sql);

		//	SQLを実行してその結果を取得する
		ResultSet rs = statement.executeQuery();

		//	検索結果の行数分フェッチを行い、取得結果をTodoインスタンスへ格納する
		while(rs.next()) {
			Todo dto = new Todo();

			//	クエリー結果をVOへ格納（あらかじめクエリー結果とdtoの変数名は一致させている）
			dto.setId(rs.getInt("id"));
			dto.setTitle(rs.getString("title"));
			dto.setTask(rs.getString("task"));
			dto.setLimitdate(rs.getTimestamp("limitdate"));
			dto.setLastupdate(rs.getTimestamp("lastupdate"));
			dto.setUserid(rs.getString("userid"));
			dto.setLabel(rs.getString("label"));
			dto.setFilename(rs.getString("filename"));

			returnList.add(dto);
		}

		return returnList;
	}

	/**
	 * 表示するタスクの番号を指定して、タスク詳細を返す。
	 * @param id 表示対象のタスクID
	 * @return
	 * @throws Exception
	 */
	public Todo detail(int id) throws Exception {
		Todo dto = new Todo();

		String sql = "SELECT id , title , task , limitdate , lastupdate , userid , label , td.status , filename " +
					"FROM todo_list td LEFT JOIN status_list stts ON stts.status = td.status where id = ?";

		// プリペアステートメントを取得し、実行SQLを渡す
		PreparedStatement statement = getPreparedStatement(sql);
		statement.setInt(1, id);

		// SQLを実行してその結果を取得する。
		ResultSet rs = statement.executeQuery();

		// 検索結果の行数分フェッチを行い、取得結果をDTOへ格納する
		while (rs.next()) {
			// クエリー結果をDTOへ格納(あらかじめクエリー結果とDTOの変数名は一致させている)
			dto.setId(rs.getInt("id"));
			dto.setTitle(rs.getString("title"));
			dto.setTask(rs.getString("task"));
			dto.setLimitdate(rs.getTimestamp("limitdate"));
			dto.setLastupdate(rs.getTimestamp("lastupdate"));
			dto.setUserid(rs.getString("userid"));
			dto.setLabel(rs.getString("label"));
			dto.setStatus(rs.getInt("status"));
			dto.setFilename(rs.getString("filename"));
		}
		return dto;
	}


	/**
	 * 新規登録処理を行う
	 * タスクIDはAutoIncrementのキー項目なので、
	 * INSERT文のSQLに含めなくても自動的に最新のIDが登録される
	 * @param dto 入力されたタスク内容
	 * @return 追加された件数
	 * @throws Exception
	 */

	public int registerInsert (Todo dto) throws Exception {

		String sql = "INSERT INTO todo_list (title, task, limitdate, lastupdate, userid, status) "
				+ " VALUES (?, ?, ?, now(), ?, 0)";

		int result = 0;
		//プリペアステートメントを取得し、実行SQLを渡す
		try {
			PreparedStatement statement = getPreparedStatement(sql);
			statement.setString(1,  dto.getTitle());
			statement.setString(2,  dto.getTask());
			statement.setString(3,  dto.getInputLimitdate());
			statement.setString(4,  dto.getUserid());

			result = statement.executeUpdate();

			//コミットを行う
			super.commit();
		}
		catch (Exception e) {
			//ロールバックを行う、スローした例外は呼び元のクラスへ渡す
			super.rollback();
			throw e;
		}

		return result;
	}

	/*
	 * 更新処理を行う
	 * @param dto
	 * @return
	 * @throws Exception
	 */

	public int registerUpdate(Todo dto) throws Exception {
		String sql = "UPDATE todo_list SET title = ? , task = ? , limitdate = ? , lastupdate = now() , userid = ? , status = ? WHERE id = ?";

		//プリペアステートメントを取得し、実行SQLを渡す
		int result = 0;
		try {
			PreparedStatement statement = getPreparedStatement(sql);
			statement.setString(1, dto.getTitle());
			statement.setString(2, dto.getTask());
			statement.setString(3, dto.getInputLimitdate());
			statement.setString(4, dto.getUserid());
			statement.setInt(5, dto.getStatus());
			statement.setInt(6, dto.getId());

			result = statement.executeUpdate();

			//コミットを行う

			super.commit();
		}
		catch(Exception e) {
			super.rollback();
			throw e;
		}

		return result;

	}

	/*
	 * 削除処理を行う。指定されたidのタスクを削除する、
	 * @param id
	 * @retuen 削除件数
	 * @throws Exception
	 */

	public int delete( int id ) throws Exception {
		String sql = "DELETE FROM todo_list where id = ?";

				//SQLを実行してその結果を取得する。
				int result = 0;
		try {
			//プリペアステートメントを取得し、実行SQLを渡す。
			PreparedStatement statement = getPreparedStatement(sql);
			statement.setInt(1, id);

			result = statement.executeUpdate();

			//コミットを行う
			super.commit();
		}
		catch (Exception e) {
			super.rollback();
			throw e;
		}

		return result;

	}

}
