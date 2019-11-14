package com.project.dictionary;

import androidx.annotation.Nullable;

public class DictionaryData {
    public String type;
    public String definition;
    public String example;
    public String word;
    public boolean isFavorite = false;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        DictionaryData other = (DictionaryData) obj;

        assert other != null;
        if (other.example.compareTo(this.example) == 0
                && other.definition.compareTo(this.definition) == 0
                && other.example.compareTo(this.example) == 0
                && other.word.compareTo(this.word) == 0) {
            return true;
        }
        return false;
    }
}

//{
//        "type": "noun",
//        "definition": "an object or group of objects wrapped in paper or packed in a box.",
//        "example": "someone had left a suspicious package"
//        }
