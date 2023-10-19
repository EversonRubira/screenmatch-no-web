package pt.com.alura.screenmatch.principal;

import pt.com.alura.screenmatch.model.DadosEpisodio;
import pt.com.alura.screenmatch.model.DadosSerie;
import pt.com.alura.screenmatch.model.DadosTemporada;
import pt.com.alura.screenmatch.model.Episodio;
import pt.com.alura.screenmatch.service.ComsumoApi;
import pt.com.alura.screenmatch.service.ConverteDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ComsumoApi consumo = new ComsumoApi();
    private ConverteDados conversor = new ConverteDados();

    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=90f04c6b";

    public void exibeMenu() {
        System.out.println("Digite o nome da Serie para busca");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        System.out.println(dados);

        List<DadosTemporada> temporadas = new ArrayList<>();

        for (int i = 1; i<=dados.totalTemporadas(); i++){
        	json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+")  + "&season=" + i + API_KEY);
        	DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
        	temporadas.add(dadosTemporada);
        }
        temporadas.forEach(System.out::println);

        //for(int i = 0; i < dados.totalTemporadas(); i++) {
        //    List<DadosEpisodio> episodiosTemporada = temporadas.get(i).episodios();
        //    for(int j = 0; j<episodiosTemporada.size(); j++) {
        //        System.out.println(episodiosTemporada.get(j).titulo());
        //    }
        //}

        //temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

        List<DadosEpisodio> dadosEpisodios = temporadas.stream()
                .flatMap(t ->t.episodios().stream())
                .collect(Collectors.toList());

        System.out.println("-------------------------------");

       /* System.out.println("\nTop 10 Episodios");
        dadosEpisodios.stream()
                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
                .peek(e->System.out.println("Primeiro filtro (N/A) " + e))
                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
                .peek(e->System.out.println("Ordenaçao " + e))
                .limit(10)
                .peek(e->System.out.println("Limite " + e))
                .map(e-> e.titulo().toUpperCase())
                .peek(e->System.out.println("Mapeamento " + e))
                .forEach(System.out::println);
*/
       List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numero(), d))
                ).collect(Collectors.toList());

        episodios.forEach(System.out::println);

        System.out.println("Digite um trecho do titulo do episodio: ");

        var trechoTitulo = leitura.nextLine();
        Optional<Episodio> episodioBuscado = episodios.stream()
                .filter(e -> e.getTitulo().toUpperCase().contains(trechoTitulo.toUpperCase()))
                .findFirst();

        if(episodioBuscado.isPresent()) {
            System.out.println("Episodio encontrado!");
            System.out.println("Temporada: " + episodioBuscado.get().getTemporada());
        }else {
            System.out.println("Episodio nao encontrado!");
        }

/*
        System.out.println("A partir de que ano vc deseja ver os episodios? ");
        var ano = leitura.nextInt();
        leitura.nextLine();

        LocalDate dataBusca = LocalDate.of(ano, 1,1);


        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        episodios.stream()
                .filter(e -> e.getDataLancamento() !=null && e.getDataLancamento().isAfter(dataBusca))
                .forEach(e -> System.out.println(
                        "Temporada:   " + e.getTemporada() +
                                " Episodio:  " + e.getTitulo() +
                                " Data Lanamento: " + e.getDataLancamento().format(formatador)
                ));
*/
        Map<Integer, Double> avaliacaoPorTemporada = episodios.stream()
                .filter(e->e.getAvaliacao() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada, Collectors.averagingDouble(Episodio::getAvaliacao)));

        System.out.println(avaliacaoPorTemporada);

    }
}