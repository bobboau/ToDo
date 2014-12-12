/**
 * 
 */
package com.bobboau.todo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import TaskList.TaskList;

/**
 * adapter that takes a TaskList and lets an android list view see what is in it
 *
 */
public class TaskListAdapter extends BaseAdapter {

	/**
	 * used for logging
	 */
	private static final String TAG = "ToDoList";
	
	/**
	 * file the todo list is serialized to
	 */
	private static final String TODO_LIST_FILE = "todo_list.ser";
	
	/**
	 * the whole point of this adapter
	 */
	public TaskList taskList;
	
	/**
	 * defines what possible modes we could be in for showing tasks
	 */
	public enum LIST_MODE {COMPLETE, INCOMPLETE};
	
	/**
	 * what our current mode is
	 */
	LIST_MODE list_mode;
	
	/**
	 * reference to the main activity
	 */
	private MainActivity activity;
	
	/**
	 * constructor
	 */
	public TaskListAdapter(MainActivity a)
	{
		activity = a;
		deserializeTaskList();
		list_mode = LIST_MODE.INCOMPLETE;
		notifyDataSetChanged();
	}

	/**
	 * pulls a TaskList object out of the either, or file system. it's one or the other, I haven't figured out which yet
	 * @return TaskList
	 */
	private void deserializeTaskList()
	{
		taskList = null;
		
		try
		{
			FileInputStream in_file = activity.openFileInput(TODO_LIST_FILE);
			ObjectInputStream in_stream = new ObjectInputStream(in_file);
			
			taskList = (TaskList)in_stream.readObject();
			
			in_stream.close();
			in_file.close();
		}
		catch(FileNotFoundException e)
		{
			taskList = new TaskList();
		}
		catch(InvalidClassException e)
		{
			//wrong serialization version
			activity.deleteFile(TODO_LIST_FILE);
			taskList = new TaskList();
		}
		catch(IOException i)
        {
            Log.e(TAG, "Error loading ToDo list. "+i.getLocalizedMessage());
        }
		catch(ClassNotFoundException c)
        {
			Log.e(TAG, "Class TaskList not found. "+c.getLocalizedMessage());
        }
		catch(Exception e)
		{
			Log.e(TAG, "Error loading ToDo list. "+e.getLocalizedMessage());
		}
	}
	
	/**
	 * pushes the task_list back into the either
	 */
	private void serializeTaskList()
	{
		try
		{
			FileOutputStream out_file = activity.openFileOutput(TODO_LIST_FILE, Context.MODE_PRIVATE);
			ObjectOutputStream out_stream = new ObjectOutputStream(out_file);
			
			out_stream.writeObject(taskList);
			
			out_stream.close();
			out_file.close();
		}
		catch(IOException i)
		{
            Log.e(TAG, "Error saving ToDo list. "+i.getLocalizedMessage());
		}
	}
	
	private List<Integer> getItems()
	{
		List<Integer> items = null;
		
		switch(list_mode)
		{
			case COMPLETE:
				items = taskList.getCompleteTasks();
			break;
			
			case INCOMPLETE:
				items = taskList.getIncompleteTasks();
			break;
		}
		
		return items;
	}

	/**
	 * gets the number of items in the task list
	 */
	@Override
	public int getCount()
	{
		return getItems().size();
	}

	/**
	 * get the handle of the item identified by the index
	 */
	@Override
	public Object getItem(int idx)
	{
		return getItems().get(idx);
	}

	/**
	 * this function is required but not really needed by us
	 */
	@Override
	public long getItemId(int idx)
	{
		return getItems().get(idx);
	}

	/**
	 * builds the thing that the user sees
	 */
	@Override
	public View getView(int idx, View view, ViewGroup parent)
	{
		if(view == null)
		{
			LayoutInflater vi = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.task, null);
		}
		
		final Integer handle = getItems().get(idx);
		
		//event handler for clicking (on the checkbox)
		((CheckedTextView)view).setOnClickListener(
			new View.OnClickListener() {
    			public void onClick(View v)
    			{
    				((CheckedTextView)v).toggle();
    				
    				if(((CheckedTextView)v).isChecked())
    				{
    					taskList.setComplete(handle);
    				}
    				else
    				{
    					taskList.setIncomplete(handle);
    				}
    				dataChanged();
    			}
			}
		);
		
		((CheckedTextView)view).setText(taskList.getDescription(handle));
		((CheckedTextView)view).setChecked(taskList.isComplete(handle));
		
		return view;
	}

	/**
	 * sets the viewing mode
	 * @param mode
	 */
	public void setMode(LIST_MODE mode)
	{
		list_mode = mode;
		notifyDataSetChanged();
	}
	
	/**
	 * adds a task
	 */
	public void addTask(String description)
	{
    	taskList.addTask(description);
    	dataChanged();
	}
	
	/**
	 * stuff to do when the data changes
	 */
	private void dataChanged()
	{
		serializeTaskList();
		notifyDataSetChanged();
	}
}
