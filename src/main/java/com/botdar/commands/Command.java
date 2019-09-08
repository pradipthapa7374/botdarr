package com.botdar.commands;

import com.botdar.discord.EmbedHelper;
import com.botdar.radarr.RadarrApi;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.List;

public enum Command {
  ADD_MOVIE("movie add", "Adds a movie using just search text (i.e., movie add Angus)") {
    @Override
    public CommandResponse execute(String command) {
      return new CommandResponse(new RadarrApi().add(command));
    }
  },
  FIND_MOVIE("movie find", "Finds a movie using radarr (i.e., movie find John Wick)") {
    @Override
    public CommandResponse execute(String command) {
      return new CommandResponse(new RadarrApi().lookup(command));
    }
  },
  MOVIE_DOWNLOADS("movie downloads", "Shows all the active movies downloading in radarr") {
    @Override
    public CommandResponse execute(String command) {
      List<MessageEmbed> embedList = new RadarrApi().downloads();
      if (embedList == null || embedList.size() == 0) {
        return new CommandResponse(EmbedHelper.createInfoMessage("No downloads currently"));
      }
      return new CommandResponse(embedList);
    }
  },
  HELP("help", "") {
    @Override
    public CommandResponse execute(String command) {
      StringBuilder stringBuilder = new StringBuilder();
      //needs to be in a code block, otherwise the spacing won't work correctly
      stringBuilder.append("```**Commands**\n");
      int maxCommandTextLength = 0;
      for (Command com : Command.values()) {
        if (com == HELP) {
          continue;
        }
        //figure out the max command identifier length so our help message is aligned
        if (maxCommandTextLength == 0 || maxCommandTextLength < com.commandText.length()) {
          maxCommandTextLength = com.commandText.length();
        }
      }

      for (Command com : Command.values()) {
        if (com == HELP) {
          continue;
        }
        //apply the additional padding if necessary
        stringBuilder.append(com.commandText + getAdditionalPadding(com.commandText.length(), maxCommandTextLength) + HELP_COMMAND_PADDING + com.description + "\n");
      }
      stringBuilder.append("```");
      return new CommandResponse(stringBuilder.toString());
    }

    private String getAdditionalPadding(int commandTextLength, int max) {
      String additionalPadding = "";
      if (commandTextLength < max) {
        for (int i = 0; i < max - commandTextLength; i++) {
          additionalPadding += " ";
        }
      }
      return additionalPadding;
    }

    private static final String HELP_COMMAND_PADDING = "                  ";
  };

  private Command(String commandText, String description) {
    this.commandText = commandText;
    this.description = description;
  }

  public String getIdentifier() {
    return commandText.toLowerCase();
  }

  public abstract CommandResponse execute(String command);

  private final String description;
  private final String commandText;
}