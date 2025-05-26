package org.escola.service;

import org.escola.model.Aluno;
import org.escola.model.Curso;
import org.escola.model.Matricula;
import org.escola.repository.AlunoRepository;
import org.escola.repository.CursoRepository;
import org.escola.repository.MatriculaRepository;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.time.Period;

public class EscolaService {
    private final EntityManager em;
    private final AlunoRepository alunoRepo;
    private final CursoRepository cursoRepo;
    private final MatriculaRepository matriculaRepo;
    private final Scanner scanner;

    public EscolaService(EntityManager em) {
        this.em = em;
        this.alunoRepo = new AlunoRepository(em);
        this.cursoRepo = new CursoRepository(em);
        this.matriculaRepo = new MatriculaRepository(em);
        this.scanner = new Scanner(System.in);
    }

    public void iniciar() {
        int opcao;
        do {
            exibirMenu();
            opcao = lerOpcao();
            processarOpcao(opcao);
        } while (opcao != 0);
    }

    private void exibirMenu() {
        System.out.println("\n=== SISTEMA DE GESTÃO ESCOLAR ===");
        System.out.println("1. Cadastrar Aluno");
        System.out.println("2. Cadastrar Curso");
        System.out.println("3. Realizar Matrícula");
        System.out.println("4. Listar Alunos");
        System.out.println("5. Listar Cursos");
        System.out.println("6. Listar Matrículas");
        System.out.println("7. Buscar Aluno por E-mail");
        System.out.println("8. Buscar Curso por Nome");
        System.out.println("9. Relatório de Engajamento");
        System.out.println("0. Sair");
        System.out.print("Opção: ");
    }

    private int lerOpcao() {
        try {
            return scanner.nextInt();
        } catch (Exception e) {
            scanner.nextLine();
            return -1;
        } finally {
            scanner.nextLine();
        }
    }

    private void processarOpcao(int opcao) {
        switch (opcao) {
            case 1 -> cadastrarAluno();
            case 2 -> cadastrarCurso();
            case 3 -> realizarMatricula();
            case 4 -> listarAlunos();
            case 5 -> listarCursos();
            case 6 -> listarMatriculas();
            case 7 -> buscarAlunoPorEmail();
            case 8 -> buscarCursoPorNome();
            case 9 -> exibirRelatorioEngajamento();
            case 0 -> System.out.println("Saindo do sistema...");
            default -> System.out.println("Opção inválida!");
        }
    }

    private void cadastrarAluno() {
        System.out.println("\n--- CADASTRO DE ALUNO ---");

        System.out.print("Nome: ");
        String nome = scanner.nextLine();

        System.out.print("E-mail: ");
        String email = scanner.nextLine();

        LocalDate dataNascimento = lerDataNascimento();
        if (dataNascimento == null) return;

        Aluno aluno = new Aluno(nome, email, dataNascimento);
        try {
            alunoRepo.salvar(aluno);
            System.out.println("Aluno cadastrado com sucesso! ID: " + aluno.getId());
        } catch (Exception e) {
            System.out.println("Erro ao cadastrar aluno: " + e.getMessage());
        }
    }

    private LocalDate lerDataNascimento() {
        System.out.print("Data de Nascimento (AAAA-MM-DD): ");
        try {
            return LocalDate.parse(scanner.nextLine());
        } catch (DateTimeParseException e) {
            System.out.println("Formato de data inválido! Use AAAA-MM-DD");
            return null;
        }
    }

    private void cadastrarCurso() {
        System.out.println("\n--- CADASTRO DE CURSO ---");

        System.out.print("Nome: ");
        String nome = scanner.nextLine();

        System.out.print("Descrição: ");
        String descricao = scanner.nextLine();

        int cargaHoraria = lerCargaHoraria();
        if (cargaHoraria <= 0) return;

        Curso curso = new Curso(nome, descricao, cargaHoraria);
        try {
            cursoRepo.salvar(curso);
            System.out.println("Curso cadastrado com sucesso! ID: " + curso.getId());
        } catch (Exception e) {
            System.out.println("Erro ao cadastrar curso: " + e.getMessage());
        }
    }

    private int lerCargaHoraria() {
        System.out.print("Carga Horária: ");
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Valor inválido! Digite um número inteiro.");
            return -1;
        }
    }

    private void realizarMatricula() {
        System.out.println("\n--- REALIZAR MATRÍCULA ---");

        listarAlunos();
        Long alunoId = lerId("ID do Aluno: ");
        if (alunoId == null) return;

        listarCursos();
        Long cursoId = lerId("ID do Curso: ");
        if (cursoId == null) return;

        Aluno aluno = alunoRepo.buscarPorId(alunoId);
        Curso curso = cursoRepo.buscarPorId(cursoId);

        if (aluno == null || curso == null) {
            System.out.println("Aluno ou curso não encontrado!");
            return;
        }

        Matricula matricula = new Matricula(aluno, curso);
        try {
            matriculaRepo.salvar(matricula);
            System.out.println("Matrícula realizada com sucesso! ID: " + matricula.getId());
        } catch (Exception e) {
            System.out.println("Erro ao realizar matrícula: " + e.getMessage());
        }
    }

    private Long lerId(String mensagem) {
        System.out.print(mensagem);
        try {
            return Long.parseLong(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ID inválido! Digite um número.");
            return null;
        }
    }

    private void listarAlunos() {
        try {
            List<Aluno> alunos = alunoRepo.buscarTodos();

            if (alunos.isEmpty()) {
                System.out.println("\nNenhum aluno cadastrado.");
                return;
            }

            System.out.println("\n--- LISTA DE ALUNOS ---");
            alunos.forEach(aluno -> System.out.printf("%d - %s | %s | Nasc: %s%n",
                    aluno.getId(),
                    aluno.getNome(),
                    aluno.getEmail(),
                    aluno.getDataNascimento()));
        } catch (Exception e) {
            System.out.println("Erro ao listar alunos: " + e.getMessage());
        }
    }

    private void listarCursos() {
        try {
            List<Curso> cursos = cursoRepo.buscarTodos();

            if (cursos.isEmpty()) {
                System.out.println("\nNenhum curso cadastrado.");
                return;
            }

            System.out.println("\n--- LISTA DE CURSOS ---");
            cursos.forEach(curso -> System.out.printf("%d - %s | %dh | %s%n",
                    curso.getId(),
                    curso.getNome(),
                    curso.getCargaHoraria(),
                    curso.getDescricao()));
        } catch (Exception e) {
            System.out.println("Erro ao listar cursos: " + e.getMessage());
        }
    }

    private void listarMatriculas() {
        try {
            List<Matricula> matriculas = matriculaRepo.buscarTodasComDetalhes();

            if (matriculas.isEmpty()) {
                System.out.println("\nNenhuma matrícula cadastrada.");
                return;
            }

            System.out.println("\n--- LISTA DE MATRÍCULAS ---");
            matriculas.forEach(matricula -> System.out.printf("%d - Aluno: %s | Curso: %s | Data: %s%n",
                    matricula.getId(),
                    matricula.getAluno().getNome(),
                    matricula.getCurso().getNome(),
                    matricula.getDataMatricula()));
        } catch (Exception e) {
            System.out.println("Erro ao listar matrículas: " + e.getMessage());
        }
    }

    private void buscarAlunoPorEmail() {
        System.out.print("\nDigite o e-mail do aluno: ");
        String email = scanner.nextLine();

        try {
            Aluno aluno = alunoRepo.buscarPorEmail(email);

            if (aluno == null) {
                System.out.println("Aluno não encontrado!");
                return;
            }

            System.out.println("\n--- DADOS DO ALUNO ---");
            System.out.printf("ID: %d%nNome: %s%nE-mail: %s%nData Nascimento: %s%n",
                    aluno.getId(),
                    aluno.getNome(),
                    aluno.getEmail(),
                    aluno.getDataNascimento());
        } catch (Exception e) {
            System.out.println("Erro ao buscar aluno: " + e.getMessage());
        }
    }

    private void buscarCursoPorNome() {
        System.out.print("\nDigite o nome (ou parte) do curso: ");
        String nome = scanner.nextLine();

        try {
            List<Curso> cursos = cursoRepo.buscarPorNome(nome);

            if (cursos.isEmpty()) {
                System.out.println("Nenhum curso encontrado!");
                return;
            }

            System.out.println("\n--- CURSOS ENCONTRADOS ---");
            cursos.forEach(curso -> System.out.printf("%d - %s | %dh | %s%n",
                    curso.getId(),
                    curso.getNome(),
                    curso.getCargaHoraria(),
                    curso.getDescricao()));
        } catch (Exception e) {
            System.out.println("Erro ao buscar cursos: " + e.getMessage());
        }
    }

    public void exibirRelatorioEngajamento() {
        System.out.println("\n=== RELATÓRIO AVANÇADO DE ENGAJAMENTO ===");
        System.out.println("Data de geração: " + LocalDate.now() + "\n");

        List<Curso> cursos = cursoRepo.buscarTodos();
        LocalDate dataLimite = LocalDate.now().minusDays(30);

        if (cursos.isEmpty()) {
            System.out.println("Nenhum curso cadastrado para gerar relatório.");
            return;
        }

        // Cabeçalho
        System.out.println("+----------------------------+-----------+--------------+---------------------+");
        System.out.println("| Curso                      | Alunos    | Média Idade  | Matrículas (30 dias)|");
        System.out.println("+----------------------------+-----------+--------------+---------------------+");

        for (Curso curso : cursos) {
            Long totalAlunos = matriculaRepo.contarMatriculasPorCurso(curso.getId());
            Double mediaIdade = matriculaRepo.calcularMediaIdadePorCurso(curso.getId());
            Long matriculasRecentes = matriculaRepo.contarMatriculasRecentesPorCurso(
                    curso.getId(), dataLimite);


            String mediaFormatada = mediaIdade != null ?
                    String.format("%.1f anos", mediaIdade).replace(",", ".") : "N/A";

            System.out.printf("| %-26s | %-9d | %-12s | %-19d |%n",
                    curso.getNome().length() > 26 ? curso.getNome().substring(0, 23) + "..." : curso.getNome(),
                    totalAlunos,
                    mediaFormatada,
                    matriculasRecentes);
        }
        System.out.println("+----------------------------+-----------+--------------+---------------------+");
    }
}