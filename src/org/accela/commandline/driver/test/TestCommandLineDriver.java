package org.accela.commandline.driver.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.accela.commandline.driver.CommandHandler;
import org.accela.commandline.driver.CommandLineDriver;
import org.junit.Test;

public class TestCommandLineDriver
{
	@Test
	public void testCommandLineSplit()
	{
		String line = " \" C\"  AB \"CD\"EF  G\"H I\" JKL CC \"MN O D";
		InputStream ins = new ByteArrayInputStream(line.getBytes());
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		CommandLineDriver driver = new CommandLineDriver(ins, os);

		driver.addHandler(new CommandHandler()
		{

			@Override
			public String getTargetCommand()
			{
				return "C";
			}

			@Override
			public String handleCommand(String[] args)
			{
				return "Error!";
			}

		});
		driver.addHandler(new CommandHandler()
		{

			@Override
			public String getTargetCommand()
			{
				return " C";
			}

			@Override
			public String handleCommand(String[] args)
			{
				String ret = getTargetCommand() + "|";
				for (String arg : args)
				{
					ret += arg + "|";
				}
				return ret;
			}

		});

		try
		{
			driver.handleNextCommand();
		} catch (IOException ex)
		{
			ex.printStackTrace();
			assert (false);
		}

		String result = os.toString();
		System.out.println(":" + result + ":");
		assert (result.equals("Command> C|AB|CD|EF|G|H I|JKL|CC|MN O D|\r\n"));
	}

	@Test
	public void testBadReturn()
	{
		String line = " ABC ";
		InputStream ins = new ByteArrayInputStream(line.getBytes());
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		CommandLineDriver driver = new CommandLineDriver(ins, os);

		driver.addHandler(new CommandHandler()
		{

			@Override
			public String getTargetCommand()
			{
				return "ABC";
			}

			@Override
			public String handleCommand(String[] args)
			{
				return null;
			}

		});

		try
		{
			driver.handleNextCommand();
		} catch (IOException ex)
		{
			ex.printStackTrace();
			assert (false);
		}
		
		String result = os.toString();
		System.out.println("|" + result + "|");
		assert (result.equals("Command>\r\n"));
	}

	@Test
	public void testCommandNotFound()
	{
		String line = "A BC ";
		InputStream ins = new ByteArrayInputStream(line.getBytes());
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		CommandLineDriver driver = new CommandLineDriver(ins, os);

		driver.addHandler(new CommandHandler()
		{

			@Override
			public String getTargetCommand()
			{
				return "ABC";
			}

			@Override
			public String handleCommand(String[] args)
			{
				return "Error!";
			}

		});

		try
		{
			driver.handleNextCommand();
		} catch (IOException ex)
		{
			ex.printStackTrace();
			assert (false);
		}
		
		String result = os.toString();
		System.out.println("|" + result + "|");
		assert (result.equals("Command>Command not found!\r\n"));
	}
	
	private String cmd = null;
	private boolean goon = true;

	@Test
	public void testRealInteractive()
	{
		cmd = null;
		goon = true;

		CommandLineDriver driver = new CommandLineDriver(System.in, System.out);
		for (int i = 0; i < 26; i++)
		{
			cmd = "" + (char) ('A' + i);
			driver.addHandler(new CommandHandler()
			{

				private String target = new String(cmd);

				@Override
				public String getTargetCommand()
				{
					return target;
				}

				@Override
				public String handleCommand(String[] args)
				{
					String ret = "Echo: " + getTargetCommand();
					for (String arg : args)
					{
						ret += " " + arg;
					}
					return ret;
				}

			});
		}

		driver.addHandler(new CommandHandler()
		{

			private String target = "exit";

			@Override
			public String getTargetCommand()
			{
				return target;
			}

			@Override
			public String handleCommand(String[] args)
			{
				goon = false;
				return "Exiting...";
			}

		});

		while (goon)
		{
			try
			{
				driver.handleNextCommand();
			} catch (IOException ex)
			{
				ex.printStackTrace();
				assert (false);
			}
		}
	}
}
