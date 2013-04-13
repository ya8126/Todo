package todo.dao;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;
import org.slim3.tester.AppEngineTestCase;

import rootPackage.NoSuchTodoException;
import todo.model.Todo;
import todo.test.TestUtil;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.User;
import com.sun.istack.internal.NotNull;

public class TodoDaoTest extends AppEngineTestCase {

    private TodoDao dao = new TodoDao(new User("test@example.com", "test", "user01"));

    @Test
    public void test() throws Exception {
        assertThat(dao, is(notNullValue()));
    }

    private Key key;

    private Key othersKey;

    @Before
    public void setUp() throws Exception {

        super.setUp();
       
        key = Datastore.put(TestUtil.newTodo("user01", "todo1", false));
        Datastore.put(TestUtil.newTodo("user01", "todo2", false));
        Datastore.put(TestUtil.newTodo("user01", "todo3", true));
        Datastore.put(TestUtil.newTodo("user01", "todo4", true));

        othersKey = Datastore.put(TestUtil.newTodo("user02", "todo5", false));
        Datastore.put(TestUtil.newTodo("user02", "todo6", true));
    }
//
//  create
//
    @Test
    public void create(){
        int before = tester.count(Todo.class);
        
        Todo todo = dao.create("メールを書く");
        assertThat(todo, is(notNullValue()));
        assertThat(todo.getBody(), is("メールを書く"));
        assertThat(todo.isFinished(), is(false));
        assertThat(todo.getCreatedAt(), is(notNullValue()));
        assertThat(todo.getFinishedAt(), is(nullValue()));
        
        assertThat(tester.count(Todo.class), is(before + 1));
        
        todo = Datastore.get(Todo.class, todo.getKey());
        assertThat(todo.getBody(), is("メールを書く"));
        assertThat(todo.isFinished(), is(false));
        assertThat(todo.getCreatedAt(), is(notNullValue()));
        assertThat(todo.getFinishedAt(), is(nullValue()));       
    }
    
    @Test(expected=NullPointerException.class)
    public void create_bodyがnull(){
        dao.create(null);
    }
//
//  update
//   
    @Test
    public void update() throws NoSuchTodoException{
        Todo todo = dao.update(key, true);
        assertThat(todo, is(notNullValue()));
        assertThat(todo.getKey(), is(key));
        assertThat(todo.isFinished(), is(true));
        assertThat(todo.getFinishedAt(), is(notNullValue()));
        
        todo = Datastore.get(Todo.class, key);
        assertThat(todo.isFinished(), is(true));
        assertThat(todo.getFinishedAt(), is(notNullValue()));
        
        todo= dao.update(key,false);
        assertThat(todo, is(notNullValue()));
        assertThat(todo.getKey(), is(key));
        assertThat(todo.isFinished(), is(false));
        assertThat(todo.getFinishedAt(), is(nullValue()));
        
        todo = Datastore.get(Todo.class, key);
        assertThat(todo.isFinished(), is(false));
        assertThat(todo.getFinishedAt(), is(nullValue()));
    }

    @Test(expected=NoSuchTodoException.class)
    public void update_指定したキーのTODOが存在しない() throws NoSuchTodoException{
        dao.update(Datastore.createKey(Todo.class, Long.MAX_VALUE), true);
    }
    
    @Test(expected=NoSuchTodoException.class)
    public void update_別ユーザが登録したTODOのキーを指定() throws NoSuchTodoException{
        dao.update(othersKey, true);
    }
    
    @Test(expected=NullPointerException.class)
    public void update_keyがnull() throws NoSuchTodoException{
        dao.update(null, true);
    }
//
//  delete
//    
    @Test
    public void dalete() throws NoSuchTodoException{
        
        int before = tester.count(Todo.class);
        
        dao.delete(key);
        
        assertThat(tester.count(Todo.class), is(before -1));
        
        assertThat(Datastore.getOrNull(key), is(nullValue()));
 
    }

    @Test(expected=NoSuchTodoException.class)
    public void dalete_指定したキーのTODOが存在しない() throws NoSuchTodoException{
        dao.delete(Datastore.createKey(Todo.class, Long.MAX_VALUE));
    }
    
    @Test(expected=NoSuchTodoException.class)
    public void delete_別ユーザが登録したTODOのキーを指定() throws NoSuchTodoException{
        dao.delete(othersKey);
    }
    
    @Test(expected=NullPointerException.class)
    public void delete_keyがnull() throws NoSuchTodoException{
        dao.delete((Key)null);
    }   
//
//  find
//   
    @Test
    public void find(){
        List<Todo> todos = dao.find(false);
        assertThat(todos.size(), is(2));
        assertThat(todos.get(0).isFinished(), is(false));
        assertThat(todos.get(0).getUserId(), is("user01")); 
        assertThat(todos.get(1).isFinished(), is(false));
        assertThat(todos.get(1).getUserId(), is("user01")); 
        Date createdAt0 = todos.get(0).getCreatedAt();
        Date createdAt1 = todos.get(1).getCreatedAt();
        assertThat(createdAt0.compareTo(createdAt1) >= 0, is(true));
        
        todos = dao.find(true);
        assertThat(todos.size(), is(2));
        assertThat(todos.get(0).isFinished(), is(true));
        assertThat(todos.get(0).getUserId(), is("user01")); 
        assertThat(todos.get(1).isFinished(), is(true));
        assertThat(todos.get(1).getUserId(), is("user01")); 
        Date finishedAt0 = todos.get(0).getCreatedAt();
        Date finishedAt1 = todos.get(1).getCreatedAt();
        assertThat(finishedAt0.compareTo(finishedAt1) >= 0, is(true));
          
    }
}
