package co.edu.unicauca.question.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Option {

    @Column(name = "option_id", nullable = false)
    private String id;

    @Column(name = "option_text", nullable = false, length = 500)
    private String text;

    protected Option() {
    }

    public Option(String id, String text) {
        this.id = id;
        this.text = text;
    }

    public String getId() { return id; }
    public String getText() { return text; }
}
