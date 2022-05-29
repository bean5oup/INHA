//proc.java�� ������� ����
package Assignment12_13;
import genDevs.modeling.*;
import GenCol.*;
import simView.*;

public class scheduler extends ViewableAtomic
{
  
	protected job job;
	protected double scheduling_time;
	
	protected int NODE; // node�� ���μ����� ������ �޾ƿ´�.
	protected int outport_num; // ó���� 1���� �����ؼ� node�� ������ŭ �ݺ���Ű�� ���� ����

	public scheduler()
	{
		this("proc", 20, 5);
	}

	public scheduler(String name, double Scheduling_time, int node)
	{
		super(name);
		NODE = node;
		
		addInport("in");
		for(int i = 1; i <= NODE; i++) {
			addOutport("out" + i);
		}
		
		scheduling_time = Scheduling_time;
	}
  
	public void initialize()
	{
		job = new job("", '0', -1);
		outport_num = 1;
		
		holdIn("passive", INFINITY);
	}

	public void deltext(double e, message x)
	{
		Continue(e);
		if (phaseIs("passive"))
		{
			for (int i = 0; i < x.getLength(); i++)
			{
				if (messageOnPort(x, "in", i))
				{
					job = (job) x.getValOnPort("in", i);
					
					holdIn("busy", scheduling_time);
				}
			}
		}
	}
  
	public void deltint()
	{
		if (phaseIs("busy"))
		{
			outport_num++;
			if(outport_num > NODE) {
				outport_num = 1;
			}
			job = new job("", '0', -1);
			
			holdIn("passive", INFINITY);
		}
	}

	public message out()
	{
		message m = new message();
		if (phaseIs("busy"))
		{
			m.add(makeContent("out" + outport_num, job));
		}
		return m;
	}

	public String getTooltipText()
	{
		return
		super.getTooltipText()
		+ "\n" + "job: " + job.getName()
		+ "\n" + "priority: " + job.priority
		+ "\n" + "burst_time: " + job.burst_time;
	}

}

