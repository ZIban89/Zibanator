import Model.Show;
import Model.Shows;
import javafx.util.Pair;

import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by ZitZ on 02.04.2017.
 */
public class ConsoleUserInterface {
    private Pair<Boolean, String> token = new Pair<>(false, "");
    private TimetableWatcher TTW;
    private SignInHandler SIH;
    private PrintWriter out;
    private String film;

    public ConsoleUserInterface() {
        out = new PrintWriter(System.out);
        try {
            TTW = TimetableWatcher.getTimetableWatcher();
            SIH = SignInHandler.getSignInHandler();
        } catch (Exception e) {
            println("Работа в системе невозможна");
        }

    }

    private void printGreeting() {
        println("Здравствуйте!\nДля просмотра фильмов, идущих в кино, введите films.\nДля бронирования мест необходимо войти в систему.\nДля " +
                "входа введите signin name password,\nдля регистрации нового пользователя введите signup name password,\nгде name- Ваше имя в системе, password- Ваш пароль в системе.");
    }

    private void readMessage() {
        println("Введите команду");
        Scanner in = new Scanner(System.in);
        String message = in.nextLine();
        message=message.replaceAll("[ ]{2,}", " ");
        String[] words = message.split(" ");
        if (words.length == 0) {
            println("Введите команду");
            readMessage();
        }
        switch (words[0]) {
            case ("films"): {
                film = null;
                getFilms();
                readMessage();
                break;
            }
            case ("signin"): {
                if (token.getKey())
                    println("Вы уже вошли в систему. Для выхода введите signout");
                if (words.length < 3) println("Введены некорректные данные");
                else signin(words);
                readMessage();
                break;
            }
            case ("signup"): {
                if (token.getKey())
                    println("Вы уже вошли в систему. Для выхода введите signout");
                if (words.length < 3) println("Введены некорректные данные");
                else signup(words);
                readMessage();
                break;
            }
            case ("signout"): {
                token = new Pair<>(false, "");
                readMessage();
                break;
            }
            case ("film"): {
                if (words.length < 2)
                    println("Введены некорректные данные");
                else
                    getFilm(words);
                readMessage();
                break;
            }
            case ("show"): {
                if (words.length < 4)
                    println("Введены некорректные данные");
                else
                    getShow(words);
                readMessage();
                break;
            }
        }

    }



    private void getShow(String[] words) {
        try {
            LinkedList<Show> shows = TTW.getFilm(film,true).getShows();
            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yy HH:mm");
            Date date=formatter.parse(words[words.length-2] + " " + words[words.length-1]);
            StringBuilder cinemaName = new StringBuilder();
            for(int i=1;i<words.length-2;i++)
                cinemaName.append(words[i]+" ");
            cinemaName.deleteCharAt(cinemaName.length()-1);

            for (Show s : shows)
                if (cinemaName.toString().equals(s.getCinemaName()) && date.compareTo(s.getDate()) == 0) {
                    System.out.println("asdfasdfasdf");
                }


        } catch (ParseException e) {
            print("Не удалось получить информацию о фильме.");
        }
    }

    private void getFilm(String[] words) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < words.length; i++)
            sb.append(words[i] + " ");
        sb.deleteCharAt(sb.length() - 1);
        Shows shows;
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yy HH:mm");
        try {
            shows = TTW.getFilm(sb.toString(),false);
            film = shows.getFilmName();
            println("Фильм " + film + " показывают:");
            for (Show s : shows.getShows()) {
                println(s.getCinemaName() + "   " + formatter.format(s.getDate()));
            }
            println("Для просмотра информации о сеансе введите через пробел: show название кинотеатра дата время.\nДля просмотра всех фильмов введите films");
        } catch (ParseException e) {
            print("Не удалось получить информацию о фильме.");
        }

    }

    private void print(String s) {
        out.print(s);
        out.flush();
    }

    private void println(String s) {
        out.println(s);
        out.flush();
    }

    private void signin(String[] words) {
        String password = words[words.length - 1];
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < words.length - 1; i++)
            sb.append(words[i] + " ");
        sb.deleteCharAt(sb.length() - 1);
        String name = sb.toString();
        token = SIH.signIn(name, password);
        if (token.getKey()) println("Вход в систему выполнен успешно.");
        else println("Не удалось выполнить вход. Проверьте имя и пароль.");
    }

    private void getFilms() {
        HashMap<String, Pair<Date, Date>> map = null;
        try {
            map = TTW.getFilms();
        } catch (ParseException e) {
            println("Не удалось получить список файлов");
        }
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yy");
        println("Фильмы в кино:");
        for (Map.Entry<String, Pair<Date, Date>> e : map.entrySet()) {
            println(e.getKey() + " идет с " + formatter.format(e.getValue().getKey()) + " по " + formatter.format(e.getValue().getValue()));
        }
        println("Для просмотра информации о выбранном фильме введите film filmName,\nгде filmName- название фильма.");
    }

    private void signup(String[] words) {
        String password = words[words.length - 1];
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < words.length - 1; i++)
            sb.append(words[i] + " ");
        sb.deleteCharAt(sb.length() - 1);
        String name = sb.toString();
        boolean isSuccessful = SIH.signUp(name, password);
        if (isSuccessful) println("Новый пользователь зарегистрирован.");
        else println("Не удалось создать нового пользователя. Возможно пользователь с таким именем уже существует.");
    }

    public static void main(String[] args) {
        ConsoleUserInterface CUI = new ConsoleUserInterface();
        CUI.printGreeting();
        CUI.readMessage();
    }


}
