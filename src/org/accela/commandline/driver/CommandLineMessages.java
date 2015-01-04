package org.accela.commandline.driver;

public class CommandLineMessages
{
	private String commandPrompt = "Command>";

	public String getCommandPrompt()
	{
		return commandPrompt;
	}

	public void setCommandPrompt(String commandPrompt)
	{
		if (null == commandPrompt)
		{
			throw new NullPointerException("commandPrompt should not be null");
		}
		this.commandPrompt = commandPrompt;
	}

	private String commandNotFound = "Command not found!";

	public String getCommandNotFound()
	{
		return commandNotFound;
	}

	public void setCommandNotFound(String commandNotFound)
	{
		if (null == commandNotFound)
		{
			throw new NullPointerException("commandNotFound should not be null");
		}
		this.commandNotFound = commandNotFound;
	}

}
