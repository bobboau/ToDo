package com.bobboau.todo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;

import com.bobboau.todo.TaskListAdapter.LIST_MODE;

/**
 * 
 * main class of the application, effectively IS the application
 *
 */
public class MainActivity extends Activity {

	TaskListAdapter task_list_adapter;
    Switch completeStatus;
    ListView task_list_view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button add_new_button = (Button) findViewById(R.id.btn_new);
        completeStatus = (Switch) findViewById(R.id.switch_complete_status);
        task_list_view = (ListView) findViewById(R.id.view_task_list);

        setUpTaskListAdapter();
        completeStatus.setChecked(false);
        showIncomplete();

        add_new_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v)
			{
				addNew();
			}
		});

        completeStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    showComplete();
                }
                else {
                    showIncomplete();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    private void showComplete()
    {
    	task_list_adapter.setMode(LIST_MODE.COMPLETE);
	}

    private void showIncomplete()
    {
    	task_list_adapter.setMode(LIST_MODE.INCOMPLETE);
    }
    
    private void addNew()
    {
    	//get the description from the user
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);

    	alert.setTitle("New Task");
    	alert.setMessage("Enter the description of your new task");
    	
    	final EditText input = new EditText(this);
    	input.setEnabled(true);
    	alert.setView(input);

    	alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			String description = input.getText().toString();
			task_list_adapter.addTask(description);
		  }
		});

		alert.setNegativeButton("Cancel", null);
		
		alert.show();
    }

    public void setUpTaskListAdapter() {
        //set up the listview and its adapter
        task_list_adapter = new TaskListAdapter(this);
        task_list_view.setAdapter(
                task_list_adapter
        );
    }
}
