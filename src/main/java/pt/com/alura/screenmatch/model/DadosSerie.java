package pt.com.alura.screenmatch.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosSerie(@JsonAlias("Title") String titulo,
                         @JsonAlias("Runtime")String duracao,
                         @JsonAlias("imdbRating") String avaliacao,
                         @JsonAlias("imdbVotes") String votos) {
}
