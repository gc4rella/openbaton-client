/*
 * Copyright (c) 2016 Open Baton (http://www.openbaton.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.openbaton.cli;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import jline.console.ConsoleReader;
import jline.console.completer.ArgumentCompleter;
import jline.console.completer.Completer;
import jline.console.completer.FileNameCompleter;
import jline.console.completer.StringsCompleter;
import org.openbaton.catalogue.mano.descriptor.NetworkServiceDescriptor;
import org.openbaton.catalogue.mano.descriptor.VNFDependency;
import org.openbaton.catalogue.mano.descriptor.VirtualNetworkFunctionDescriptor;
import org.openbaton.catalogue.mano.record.NetworkServiceRecord;
import org.openbaton.catalogue.mano.record.VNFCInstance;
import org.openbaton.catalogue.mano.record.VNFRecordDependency;
import org.openbaton.catalogue.mano.record.VirtualNetworkFunctionRecord;
import org.openbaton.cli.exceptions.CommandLineException;
import org.openbaton.cli.model.Command;
import org.openbaton.cli.util.PrintFormat;
import org.openbaton.cli.util.Utils;
import org.openbaton.sdk.NFVORequestor;
import org.openbaton.sdk.api.annotations.Help;
import org.openbaton.sdk.api.util.AbstractRestAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Created by lto on 14/07/15. */
public class NFVOCommandLineInterface {

  private static Logger log = LoggerFactory.getLogger(NFVOCommandLineInterface.class);

  private static final String VERSION = "1";

  private static final LinkedHashMap<String, LinkedList<Command>> commandMap =
      new LinkedHashMap<>();
  private static final LinkedHashMap<String, String> helpCommandMap =
      new LinkedHashMap<String, String>() {
        {
          put("help", "print the usage");
        }
      };

  /** Print out how to use the OpenBaton command line interface. */
  public static void usage() {

    log.info(
        "\n"
            + " _______  _______________   ____________            _________ .____    .___ \n"
            + " \\      \\ \\_   _____/\\   \\ /   /\\_____  \\           \\_   ___ \\|    |   |   |\n"
            + " /   |   \\ |    __)   \\   Y   /  /   |   \\   ______ /    \\  \\/|    |   |   |\n"
            + "/    |    \\|     \\     \\     /  /    |    \\ /_____/ \\     \\___|    |___|   |\n"
            + "\\____|__  /\\___  /      \\___/   \\_______  /          \\______  /_______ \\___|\n"
            + "        \\/     \\/                       \\/                  \\/        \\/    ");
    log.info("OpenBaton's NFVO Command Line Interface");
    System.out.println("/~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~/");
    System.out.println("Available commands are");
    String format = "%-80s%s%n";
    for (Object entry : helpCommandMap.entrySet()) {
      System.out.printf(
          format,
          ((Map.Entry) entry).getKey().toString() + ":",
          ((Map.Entry) entry).getValue().toString());
    }
    System.out.println("/~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~/");
  }

  /**
   * Print out how to use a specific command.
   *
   * @param s The command
   */
  public static void helpUsage(String s) {
    for (Object entry : helpCommandMap.entrySet()) {
      String format = "%-80s%s%n";
      if (((Map.Entry) entry).getKey().toString().startsWith(s)
          || ((Map.Entry) entry).getKey().toString().startsWith(s + "-")) {
        System.out.printf(
            format,
            ((Map.Entry) entry).getKey().toString() + ":",
            ((Map.Entry) entry).getValue().toString());
      }
    }
  }

  private static void helpCommand(String command) {
    Command cmd = commandMap.get(command).getFirst();
    System.out.println();
    System.out.println("/~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~/");
    System.out.print("Usage: " + command + " ");
    for (Class c : cmd.getParams()) {
      System.out.print("<" + c.getSimpleName() + ">");
    }
    System.out.println();
    System.out.println();
    String format = "%-80s%s%n";
    System.out.println("Where:");
    for (Class c : cmd.getParams())
      System.out.printf(format, "<" + c.getSimpleName() + ">  is a: ", c.getName());
    System.out.println("/~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~/");
  }

  public static void main(String[] args)
      throws IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {

    ConsoleReader reader = getConsoleReader();
    Properties properties = new Properties();

    readEnvVars(properties);
    getProperty(reader, properties, "NFVO_USERNAME", "admin");
    getProperty(reader, properties, "NFVO_PASSWORD", "openbaton");
    getProperty(reader, properties, "NFVO_PROJECT_ID", "default");
    getProperty(reader, properties, "NFVO_SSL_ENABLED", "true");
    getProperty(reader, properties, "NFVO_IP", "127.0.0.1");
    getProperty(reader, properties, "NFVO_PORT", "8443");
    getProperty(reader, properties, "NFVO_API_VERSION", VERSION);

    NFVORequestor nfvo =
        new NFVORequestor(
            properties.getProperty("NFVO_USERNAME"),
            properties.getProperty("NFVO_PASSWORD"),
            properties.getProperty("NFVO_PROJECT_ID"),
            Boolean.parseBoolean(properties.getProperty("NFVO_SSL_ENABLED")),
            properties.getProperty("NFVO_IP"),
            properties.getProperty("NFVO_PORT"),
            properties.getProperty("NFVO_API_VERSION"));

    fillCommands(nfvo);

    List<Completer> completors = new LinkedList<Completer>();
    completors.add(new StringsCompleter(helpCommandMap.keySet()));
    completors.add(new FileNameCompleter());

    reader.addCompleter(new ArgumentCompleter(completors));
    reader.setPrompt(
        "\u001B[135m" + properties.get("NFVO_USERNAME") + "@[\u001B[32mopen-baton\u001B[0m]~> ");

    try {
      reader.setPrompt(
          "\u001B[135m" + properties.get("NFVO_USERNAME") + "@[\u001B[32mopen-baton\u001B[0m]~> ");

      int find = 0;

      for (Object entry : helpCommandMap.entrySet()) {
        String format = "%-80s%s%n";
        String search = args[0] + "-";
        if (((Map.Entry) entry).getKey().toString().equals(args[0])) {
          find++;
        }
      }

      if (find > 0) { //correct comand
        if (args.length == 1 && args[0].equalsIgnoreCase("help")) { // case: ./openbaton.sh help
          usage();
          exit(0);
        }

        if (args[args.length - 1].equalsIgnoreCase("help")) { // case: ./openbaton.sh [command] help
          helpUsage(args[0]);
          exit(0);
        }

        if (args.length != 1
            && args[0].equalsIgnoreCase("help")) { // case: ./openbaton.sh help something...
          System.out.println(
              "Type the following to get help for the usage of a command: \n./openbaton.sh [command] help");
          exit(1);
        }

        //execute comand
        try {
          String result = PrintFormat.printResult(args[0], executeCommand(args));
          System.out.println(result);
          exit(0);

        } catch (CommandLineException ce) {
          System.out.println("Error: " + ce.getMessage());
          if (log.isDebugEnabled()) ce.getCause().printStackTrace();
          exit(1);
        } catch (Exception e) {
          e.printStackTrace();
          log.error("Error while invoking command");
          exit(1);
        }
      } else { //wrong comand
        for (Object entry : helpCommandMap.entrySet()) {
          String format = "%-80s%s%n";
          if (((Map.Entry) entry).getKey().toString().startsWith(args[0])) {
            System.out.printf(
                format,
                ((Map.Entry) entry).getKey().toString() + ":",
                ((Map.Entry) entry).getValue().toString());
            find++;
          }
        }
        if (find == 0) {

          System.out.println("OpenBaton's NFVO Command Line Interface");
          System.out.println("/~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~/");
          System.out.println(args[0] + ": comand not found");
          exit(1);
        }
      }

    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

  private static void getProperty(
      ConsoleReader reader, Properties properties, String property, String defaultProperty) {
    if (properties.get(property) == null) {
      //log.warn(property + " property was not found neither in the file [" + CONFIGURATION_FILE + "] nor in Environment Variables");
      try {
        String insertedProperty = reader.readLine(property + "[" + defaultProperty + "]: ");
        if (insertedProperty == null) {
          insertedProperty = defaultProperty;
        }
        properties.put(property, insertedProperty);
      } catch (IOException e) {
        System.out.println("Error while reading from input");
        exit(1);
      }
    }
  }

  private static Command validateParametersAndGetCommand(String[] line)
      throws CommandLineException {
    Iterator<String> commandLineIterator = Arrays.asList(line).iterator();
    String commandName = "";
    if (commandLineIterator.hasNext()) commandName = commandLineIterator.next();
    LinkedList<String> params = new LinkedList<>();
    while (commandLineIterator.hasNext()) {
      params.add(commandLineIterator.next());
    }
    LinkedList<Command> commandList = commandMap.get(commandName);
    if (commandList == null || commandList.size() == 0) {
      throw new CommandLineException("Command: " + commandName + " not found!");
    }

    Command command = null;
    boolean paramTypeCorrect = true;
    for (Command c : commandList) {
      // check the number of arguments given in the cli to determine which command to use if there are multiple methods with the same name
      if (c.getMethod().getParameterTypes().length == params.size()) {
        command = c;
        // check if the parameters passed are correct, namely if a passed file exists.
        // If in the future the cli will accept other parameters than ids and paths to files, this has to be changed.
        paramTypeCorrect = true;
        int pos = 0;
        for (Type t : command.getMethod().getParameterTypes()) {
          if (!t.equals(String.class) && !t.equals(HashMap.class) && !t.equals(ArrayList.class)) {
            try {
              File f = new File(params.get(pos));
              if (!f.exists()) paramTypeCorrect = false;
            } catch (Exception e) {
              paramTypeCorrect = false;
            }
          }
          pos++;
        }
        if (paramTypeCorrect) break;
      }
    }

    if (!paramTypeCorrect) throw new CommandLineException("Missing filepath parameter.");

    if (command == null) throw new CommandLineException("Wrong number of parameters passed.");

    return command;
  }

  /**
   * Try to execute the user's command line input.
   *
   * @param line
   * @return the object returned by the execution of the command line input
   * @throws InvocationTargetException
   * @throws IllegalAccessException
   * @throws FileNotFoundException
   * @throws CommandLineException
   */
  private static Object executeCommand(String[] line)
      throws InvocationTargetException, IllegalAccessException, FileNotFoundException,
          CommandLineException {
    Iterator<String> commandLineIterator = Arrays.asList(line).iterator();

    // remove command to get just the parameters
    if (commandLineIterator.hasNext()) commandLineIterator.next();

    Command command = validateParametersAndGetCommand(line);
    handleExceptionalCommandNames(command);
    log.trace(
        "invoking method: "
            + command.getMethod().getName()
            + " with parameters: "
            + command.getParams());

    List<Object> params = new LinkedList<>();
    Gson gson = new GsonBuilder().create();
    for (Type t : command.getParams()) {
      log.trace("type is: " + t);
      if (t.equals(String.class)) { //for instance an id
        params.add(commandLineIterator.next());
      } else if (t.equals(HashMap.class)) {
        String pathname = commandLineIterator.next();
        log.trace("the path is: " + pathname);
        File f = new File(pathname);
        FileInputStream fileInputStream = new FileInputStream(f);
        String file = getString(fileInputStream);
        log.trace(file);
        HashMap casted;
        try {
          casted = gson.fromJson(file, HashMap.class);
        } catch (JsonParseException je) {
          throw new CommandLineException(
              "The provided json file could not be cast to a HashMap ", je.getCause());
        }
        log.trace("Parameter added is: " + casted);
        params.add(casted);
      } else if (t.equals(ArrayList.class)) {
        String pathname = commandLineIterator.next();
        log.trace("the path is: " + pathname);
        File f = new File(pathname);
        FileInputStream fileInputStream = new FileInputStream(f);
        String file = getString(fileInputStream);
        log.trace(file);
        ArrayList casted;
        try {
          casted = gson.fromJson(file, ArrayList.class);
        } catch (JsonParseException je) {
          throw new CommandLineException(
              "The provided json file could not be cast to an ArrayList ", je.getCause());
        }
        log.trace("Parameter added is: " + casted);
        params.add(casted);
      } else { // for instance waiting for an obj so passing a file
        String pathname = commandLineIterator.next();
        log.trace("the path is: " + pathname);
        File f = new File(pathname);
        FileInputStream fileInputStream = new FileInputStream(f);
        String file = getString(fileInputStream);
        log.trace(file);
        log.trace("waiting for an object of type " + command.getClazz().getName());
        Object casted = null;
        try {
          casted = command.getClazz().cast(gson.fromJson(file, command.getClazz()));
        } catch (JsonParseException je) {
          throw new CommandLineException(
              "The provided json file could not be cast to an object of type "
                  + command.getClazz().getSimpleName(),
              je.getCause());
        }
        log.trace("Parameter added is: " + casted);
        params.add(casted);
      }
    }
    String parameters = "";
    for (Object type : params) {
      parameters += type.getClass().getSimpleName() + " ";
    }
    log.trace(
        "invoking method: " + command.getMethod().getName() + " with parameters: " + parameters);
    return command.getMethod().invoke(command.getInstance(), params.toArray());
  }

  /**
   * This method modifies commands which do not use the object provided by their names. For example
   * the command NetworkServiceRecord-createVNFCInstance will not create a NetworkServiceRecord but
   * a VNFCInstance. Therefore the clazz attribute in the command object has to be changed from
   * NetworkServiceRecord to VNFCInstance.
   *
   * @param command
   */
  private static void handleExceptionalCommandNames(Command command) {
    if (command.getClazz().equals(NetworkServiceRecord.class)) {
      if (command.getMethod().getName().equals("createVNFCInstance"))
        command.setClazz(VNFCInstance.class);
      if (command.getMethod().getName().equals("createVNFR"))
        command.setClazz(VirtualNetworkFunctionRecord.class);
      if (command.getMethod().getName().equals("postVNFDependency"))
        command.setClazz(VNFRecordDependency.class);
      if (command.getMethod().getName().equals("updateVNFDependency"))
        command.setClazz(VNFRecordDependency.class);
    }
    if (command.getClazz().equals(NetworkServiceDescriptor.class)) {
      if (command.getMethod().getName().equals("createVNFD"))
        command.setClazz(VirtualNetworkFunctionDescriptor.class);
      if (command.getMethod().getName().equals("createVNFDependency"))
        command.setClazz(VNFDependency.class);
    }
  }

  private static String getString(FileInputStream fileInputStream) {
    StringBuilder builder = new StringBuilder();
    int ch;
    try {
      while ((ch = fileInputStream.read()) != -1) {
        builder.append((char) ch);
      }
    } catch (IOException e) {
      e.printStackTrace();
      exit(333);
    }
    return builder.toString();
  }

  /**
   * Fill the commandMap and helpCommandMap with the available commands from the sdk.
   *
   * @param nfvoRequestor
   */
  private static void fillCommands(NFVORequestor nfvoRequestor) throws FileNotFoundException {
    getMethods(nfvoRequestor.getNetworkServiceRecordAgent());
    getMethods(nfvoRequestor.getConfigurationAgent());
    getMethods(nfvoRequestor.getEventAgent());
    getMethods(nfvoRequestor.getVNFFGAgent());
    getMethods(nfvoRequestor.getVimInstanceAgent());
    getMethods(nfvoRequestor.getNetworkServiceDescriptorAgent());
    getMethods(nfvoRequestor.getVirtualNetworkFunctionDescriptorAgent());
    getMethods(nfvoRequestor.getVirtualLinkAgent());
    getMethods(nfvoRequestor.getVNFPackageAgent());
    getMethods(nfvoRequestor.getKeyAgent());
    getMethods(nfvoRequestor.getProjectAgent());
    getMethods(nfvoRequestor.getUserAgent());
  }

  /**
   * Fill the commandMap and helpCommandMap with the available commands offered by a specific
   * AbstractRestAgent from the sdk.
   *
   * @param agent
   */
  private static void getMethods(AbstractRestAgent agent) {
    String className = agent.getClass().getSimpleName();
    log.trace(className);
    Class clazz = null;
    try {
      clazz = (Class) agent.getClass().getSuperclass().getDeclaredMethod("getClazz").invoke(agent);
    } catch (InvocationTargetException e) {
      e.printStackTrace();
      exit(123);
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
      exit(123);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
      exit(123);
    }
    String replacement = null;
    if (className.endsWith("RestRequest")) {
      replacement = className.substring(0, className.indexOf("RestRequest"));
    } else if (className.endsWith("RestAgent")) {
      replacement = className.substring(0, className.indexOf("RestAgent"));
    } else if (className.endsWith("Agent")) {
      replacement = className.substring(0, className.indexOf("Agent"));
    } else exit(700);
    log.trace("Clazz: " + clazz);
    log.trace("Replacement: " + replacement);

    for (Method superMethod : agent.getClass().getSuperclass().getDeclaredMethods()) {
      if (superMethod.isAnnotationPresent(Help.class)
          && !superMethod.isAnnotationPresent(Deprecated.class)) {
        boolean superMethodOverridden = false;
        for (Method subMethod : agent.getClass().getDeclaredMethods()) {
          if (Utils.methodsAreEqual(superMethod, subMethod)) superMethodOverridden = true;
        }
        if (!superMethodOverridden) {
          helpCommandMap.put(
              replacement + "-" + superMethod.getName(),
              superMethod.getAnnotation(Help.class).help().replace("{#}", replacement));
          Command command = new Command(agent, superMethod, superMethod.getParameterTypes(), clazz);
          if (commandMap.containsKey(replacement + "-" + superMethod.getName())) {
            commandMap.get(replacement + "-" + superMethod.getName()).add(command);
          } else {
            LinkedList commandList = new LinkedList();
            commandList.add(command);
            commandMap.put(replacement + "-" + superMethod.getName(), commandList);
          }
        }
      }
    }

    for (Method method : agent.getClass().getDeclaredMethods()) {
      if (method.isAnnotationPresent(Help.class) && !method.isAnnotationPresent(Deprecated.class)) {
        Command command = new Command(agent, method, method.getParameterTypes(), clazz);
        helpCommandMap.put(
            replacement + "-" + method.getName(), method.getAnnotation(Help.class).help());
        // check if key is already in map
        if (commandMap.containsKey(replacement + "-" + method.getName())) {
          commandMap.get(replacement + "-" + method.getName()).add(command);
        } else {
          LinkedList commandList = new LinkedList();
          commandList.add(command);
          commandMap.put(replacement + "-" + method.getName(), commandList);
        }
      }
    }
  }

  private static ConsoleReader getConsoleReader() {
    ConsoleReader reader = null;
    try {
      reader = new ConsoleReader();
    } catch (IOException e) {
      System.out.println("Error while creating ConsoleReader");
      exit(1);
    }
    return reader;
  }

  private static void readEnvVars(Properties properties) {
    try {
      properties.put("NFVO_USERNAME", System.getenv().get("NFVO_USERNAME"));
      properties.put("NFVO_PASSWORD", System.getenv().get("NFVO_PASSWORD"));
      properties.put("NFVO_PROJECT_ID", System.getenv().get("NFVO_PROJECT_ID"));
      properties.put("NFVO_SSL_ENABLED", System.getenv().get("NFVO_SSL_ENABLED"));
      properties.put("NFVO_IP", System.getenv().get("NFVO_IP"));
      properties.put("NFVO_PORT", System.getenv().get("NFVO_PORT"));
      properties.put("NFVO_API_VERSION", System.getenv().get("NFVO_API_VERSION"));
    } catch (NullPointerException e) {
    }
  }

  /**
   * Exit with the given status code.
   *
   * @param status
   */
  private static void exit(int status) {
    System.exit(status);
  }
}
