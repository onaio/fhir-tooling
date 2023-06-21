/* (C)2023 */
package org.smartregister.domain;

public class FCTFile {
  private final String name;
  private final String content;

  private final String firstLine;

  public FCTFile(String name, String content, String firstLine) {
    this.name = name;
    this.content = content;
    this.firstLine = firstLine;
  }

  public String getName() {
    return name;
  }

  public String getContent() {
    return content;
  }

  public String getFirstLine() {
    return firstLine;
  }
}
