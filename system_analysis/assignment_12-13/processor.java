//procQ.java�� ���
package Assignment12_13;
import genDevs.modeling.*;

import java.util.Stack;

import GenCol.*;
import simView.*;

public class processor extends ViewableAtomic
{
	
	protected Queue q;
	protected Stack<job> stop_st;
	protected job job;
	protected job processing_job;
	protected double processing_time;
	
	protected int size;
	protected int proc_num;
	protected loss_msg loss_msg;
	protected double temp_time; // atomic model�� sigma(left ta)�� �޾Ƽ� �Ҵ�
	
	public processor()
	{
		this("processor", 20, 2);
	}

	public processor(String name, double Processing_time, int Size)
	{
		super(name);
		size = Size;
    
		addInport("in");
		addOutport("out1"); // transd solved
		addOutport("out2"); // transd loss    queue�� �� ���� loss port�� �������ڴ�.
		
		proc_num = Integer.parseInt(name.substring(9));
		
		processing_time = Processing_time;
	}
	
	public void initialize()
	{
		q = new Queue();
		stop_st = new Stack<>();
		job = new job("", '0', -1);
		processing_job = new job("", '0', -1);
		temp_time = 0;
		loss_msg = new loss_msg("none", 0);
		
		holdIn("passive", INFINITY);
	}

	public void deltext(double e, message x)
	{
		Continue(e);
		if (phaseIs("passive"))
		{
			for (int i = 0; i < x.size(); i++)
			{
				if (messageOnPort(x, "in", i))
				{
					job = (job) x.getValOnPort("in", i);
					processing_job = job;
					holdIn("busy", job.burst_time);
				}
			}
		}
		else if (phaseIs("busy"))
		{
			for (int i = 0; i < x.size(); i++)
			{
				if (messageOnPort(x, "in", i))
				{
					job other_job = (job) x.getValOnPort("in", i);
					if (processing_job.priority > other_job.priority) { // ó������ job���� �켱������ ���� job�� ������ ���� ó��
						processing_job.burst_time = (int) sigma;
						stop_st.push(processing_job);
						
						processing_job = other_job;
						holdIn("busy", other_job.burst_time);
					}
					else if (processing_job.priority == other_job.priority) {
						stop_st.push(other_job);
					}
					else {
						if (q.size() < size) { // ������ q.size�� ���� �Ҵ�� q.size���� ������
							q.add(other_job);
						}
						else {
							temp_time = sigma;
							holdIn("loss", 0);
						}
					}
				}
			}
		}
	}
	
	public void deltint()
	{
		if (phaseIs("loss"))
		{ // �����ִ� ta�� temp_time�� �Ҵ�. �ٸ� �۾� ó���ϰ� ���ƿͼ��� ������ �� �ְ�
			holdIn("busy", temp_time);
		}
		else if(phaseIs("busy")) {
			if (!stop_st.isEmpty()) { // queue���� stack�� ���� Ȯ��, �켱������ �����ϸ� stack�� �����Ƿ�
				processing_job = stop_st.peek();
				stop_st.pop();
				
				holdIn("busy", processing_job.burst_time);
			}
			else if (!q.isEmpty())
			{
				job = (job) q.removeFirst();
				
				holdIn("busy", job.burst_time);
			}
			else
			{
				job = new job("", '0', -1);
				loss_msg = new loss_msg("none", 0);
				holdIn("passive", INFINITY);
			}
		}
	}

	public message out()
	{
		message m = new message();
		
		if (phaseIs("busy"))
		{
			m.add(makeContent("out1", job));
		}
		else if(phaseIs("loss")) { // processor ����� job�� ���ƴ�.
			loss_msg = new loss_msg("processor" + proc_num + " : loss a job", proc_num);
			m.add(makeContent("out2", loss_msg));
		}
		return m;
	}	
	
	public String getTooltipText()
	{
		return
        super.getTooltipText()
        + "\n" + "queue length: " + q.size()
        + "\n" + "queue itself: " + q.toString()
        + "\n" + "stack length: " + stop_st.size()
        + "\n" + "stack itself: " + stop_st.toString()
		+ "\n" + "processing_job: " + processing_job.getName()
		+ "\n" + "priority: " + processing_job.priority
		+ "\n" + "burst_time: " + processing_job.burst_time;
	}

}



