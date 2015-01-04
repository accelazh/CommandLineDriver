package org.accela.commandline.driver;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

//TODO 测试时打开断言
public class CommandLineDriver
{
	private Map<String, CommandHandler> handlers =
			new HashMap<String, CommandHandler>();

	private BufferedReader in = null;

	private PrintWriter out = null;

	private CommandLineMessages messages = new CommandLineMessages();

	public CommandLineDriver(InputStream in, OutputStream out)
	{
		if (null == in)
		{
			throw new NullPointerException("in should not be null");
		}
		if (null == out)
		{
			throw new NullPointerException("out should not be null");
		}

		this.in = new BufferedReader(new InputStreamReader(in));
		this.out = new PrintWriter(new OutputStreamWriter(out));
	}

	public CommandLineMessages getMessages()
	{
		return this.messages;
	}

	public CommandHandler addHandler(CommandHandler handler)
	{
		if (null == handler)
		{
			throw new NullPointerException("handler should not be null");
		}
		if (null == handler.getTargetCommand())
		{
			throw new NullPointerException("handler.getTargetCommand() should not be null");
		}

		return handlers.put(handler.getTargetCommand(), handler);
	}

	public CommandHandler getHandler(String targetCommand)
	{
		if (null == targetCommand)
		{
			throw new NullPointerException("targetCommand should not be null");
		}
		return handlers.get(targetCommand);
	}

	public CommandHandler removeHandler(String targetCommand)
	{
		if (null == targetCommand)
		{
			throw new NullPointerException("targetCommand should not be null");
		}
		return handlers.remove(targetCommand);
	}

	/**
	 * 命令格式： command arg1 arg2 arg3 ... 。空格是参数分隔符。当参数字符串中有空格时，两端用双引号括起即可。
	 * 此时双引号中的字符串，包括空格，会被当作整体，作为一个参数。双引号也分割参数的功能，例如"ABC"DE FG将被解
	 * 释成三个参数：ABC、DE、FG。如果引号不能够完全配对，那么最后一个引号右边的所有字符将被整体解释为一个参数。
	 */
	public void handleNextCommand() throws IOException
	{
		printCommandPrompt();

		String line = readCommandLine();
		if (line.length() <= 0)
		{
			return;
		}

		List<String> tokens = breakCommandLineIntoTokens(line);

		String result =
				handleCommand(tokens.get(0), tokens.subList(1, tokens.size())
						.toArray(new String[0]));
		if (null == result)
		{
			printCommandNotFound();
			return;
		}

		printCommandResult(result);
	}

	private void printCommandPrompt()
	{
		out.print(messages.getCommandPrompt());
		out.flush();
	}

	private String readCommandLine() throws IOException
	{
		String line = in.readLine();
		if (null == line)
		{
			throw new EOFException();
		}
		line = line.trim();

		return line;
	}

	private List<String> breakCommandLineIntoTokens(String line)
	{
		assert (line != null);
		assert (line.trim().equals(line));
		assert (line.length() >= 1);

		List<String> tokens = new LinkedList<String>();
		StringBuffer curToken = new StringBuffer();
		boolean quoted = false;
		for (int i = 0; i < line.length(); i++)
		{
			char c = line.charAt(i);

			if ('"' == c)
			{
				if (quoted)
				{
					quoted = false;
					tokens.add(curToken.toString());
					curToken = new StringBuffer();
				} else
				{
					quoted = true;
					if (curToken.length() > 0)
					{
						tokens.add(curToken.toString());
						curToken = new StringBuffer();
					} else
					{
						// do nothing
					}
				}
			} else if (' ' == c)
			{
				if (quoted)
				{
					curToken.append(c);
				} else
				{
					if (curToken.length() > 0)
					{
						tokens.add(curToken.toString());
						curToken = new StringBuffer();
					} else
					{
						// do nothing
					}
				}
			} else
			{
				curToken.append(c);
			}
		}
		tokens.add(curToken.toString());

		assert (tokens.size() >= 1);
		return tokens;
	}

	private String handleCommand(String command, String[] args)
	{
		CommandHandler handler = handlers.get(command);
		if (null == handler)
		{
			return null;
		}

		assert (handler.getTargetCommand().equals(command));
		String result = handler.handleCommand(args);
		if (null == result)
		{
			result = "";
		}

		return result;
	}

	private void printCommandNotFound()
	{
		out.println(messages.getCommandNotFound());
		out.flush();
	}

	private void printCommandResult(String result)
	{
		assert (result != null);
		out.println(result);
		out.flush();
	}
}
