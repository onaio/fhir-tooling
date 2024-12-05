/* (C)2023 */
package org.smartregister.domain;

public class FctFile {
  private final String name;
  private final String content;

  private final String firstLine;

  public FctFile(String name, String content, String firstLine) {
    this.name = name;
    this.content = content;
    this.firstLine = firstLine;
  }

  public String getName() {
    return name;
  }

  public String getNameWithoutExtension() {
    return name.substring(0, name.lastIndexOf('.'));
  }

  public String getContent() {
    return content;
  }

  public String getFirstLine() {
    return firstLine;
  }
}
