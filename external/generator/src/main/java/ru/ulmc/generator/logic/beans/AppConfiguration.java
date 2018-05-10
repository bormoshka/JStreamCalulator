package ru.ulmc.generator.logic.beans;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.File;
import java.io.Serializable;
import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AppConfiguration implements Serializable {
    private static final long serialVersionUID = 1L;
    private Map<String, String> properties = new HashMap<>();
    private Set<File> recentFiles = new TreeSet<>();
}
