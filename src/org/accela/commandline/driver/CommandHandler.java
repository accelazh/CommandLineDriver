package org.accela.commandline.driver;

public interface CommandHandler
{
	public String getTargetCommand();
	
	public String handleCommand(String[] args);
}
