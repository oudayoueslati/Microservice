package tn.esprit.examen.nomPrenomClasseExamen.entities;

public class ChatResponseDTO {
    private String reply;

    public ChatResponseDTO(String reply) {
        this.reply = reply;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }
}

