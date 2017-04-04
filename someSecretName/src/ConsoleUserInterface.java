import Model.Show;
import Model.Shows;
import Model.Tickets;
import com.sun.org.apache.xpath.internal.SourceTree;
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
    private String userName;
    private TimetableWatcher tW;
    private SignInHandler sH;
    private BookingEditor bE;
    private PrintWriter out;
    private String filmName;
    private Show requiredShow;
    private Tickets[] tickets;

    public ConsoleUserInterface() {
        out = new PrintWriter(System.out);
        try {
            tW = TimetableWatcher.getTimetableWatcher();
            sH = SignInHandler.getSignInHandler();
            bE = BookingEditor.getInstance();
        } catch (Exception e) {
            println("Работа в системе невозможна");
        }
    }

    private void printGreeting() {
        println("Здравствуйте!\nДля просмотра фильмов, идущих в кино, введите films.\nДля бронирования мест необходимо войти в систему.\nДля " +
                "входа введите signin name password,\nдля регистрации нового пользователя введите signup name password,\nгде name- Ваше имя в системе, password- Ваш пароль в системе.");
    }

    private void readMessage() {
        println("Введите команду:");
        Scanner in = new Scanner(System.in);
        String message = in.nextLine();
        message=message.replaceAll("[ ]{2,}", " ");
        String[] words = message.split(" ");
        if (words.length == 0) {
            println("Введите команду:");
            readMessage();
        }
        switch (words[0]) {
            case ("films"): {
                filmName = null;
                getFilms();
                readMessage();
                break;
            }
            case ("signin"): {
                if (token.getKey())
                    println("Вы уже вошли в систему. Для выхода введите signout");
                if (words.length < 3) printIncorrectData();
                else signin(words);
                if (token.getKey()) {
                    println("");
                }
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
                userName = null;
                readMessage();
                break;
            }
            case ("film"): {
                if (words.length < 2)
                    printIncorrectData();
                else
                    getFilm(words);
                readMessage();
                break;
            }
            case ("show"): {
                if (words.length < 4)
                    printIncorrectData();
                else
                    getShow(words);
                readMessage();
                break;
            }
            case ("booking"): {
                if (token.getKey()) {
                    if (tW.bookingSeats(filmName, requiredShow, Arrays.copyOfRange(words, 1, words.length)))
                        if (bE.addTickets(userName, requiredShow, filmName, Arrays.copyOfRange(words, 1, words.length)))
                            println("Билеты успешно забронированы.");
                        else
                            println("Что-то пошло не так. Билеты не были добавлены в Ваш профиль.");
                    else
                        println("Что-то пошло не так. Билеты не были забронированы. Возможно место уже забронировано.");
                } else println("Для бронирования мест необходимо войти в систему.");
                readMessage();
                break;
            }
            case ("tickets"): {
                if (token.getKey()) {
                    initTickets(userName);
                    println("Ваша бронь:");
                    int n = 0;
                    for (Tickets s : tickets) {
                        n++;
                        StringBuilder sb = new StringBuilder("").append(n).append(") ").append("Показ фильма ").append(s.getFilmName()).append(" пройдёт ").
                                append(new SimpleDateFormat("dd.MM.yy HH:mm").format(s.getDate())).append(" в кинотеатре ").append(s.getCinemaName()).
                                append(". Забронированные Вами места(ряд,место):");
                        for (String seat : s.getSeats()) {
                            sb.append(" ");
                            sb.append(seat);
                        }
                        println(sb.toString());
                    }

                    println("Для отмены брони введите remove номер показа места(ряд,место) через пробел");
                } else println("Для просмотра брони необходимо войти в систему.");
                readMessage();
                break;
            }

            case ("remove"): {
                if (tickets != null)
                    try {
                        if (bE.removeTicket(userName, tickets[Integer.parseInt(words[1]) - 1], Arrays.copyOfRange(words, 2, words.length)))
                            tW.removeTickets(tickets[Integer.parseInt(words[1]) - 1], Arrays.copyOfRange(words, 2, words.length));
                        else println("Вы не бронировали данные места.");

                    } catch (Exception e) {
                    }
                else
                    println("Сначала необходимо войти в систему и получить список забронированных Вами мест");
            }

            default: {
                printIncorrectData();
                readMessage();
            }
        }
    }


    private void initTickets(String userName) {
        tickets = bE.watchTickets(userName);
    }

    private void getShow(String[] words) {
        try {
            LinkedList<Show> shows = tW.getFilm(filmName, true).getShows();
            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yy HH:mm");
            Date date=formatter.parse(words[words.length-2] + " " + words[words.length-1]);
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < words.length - 2; i++) {
                sb.append(words[i]);
                sb.append(" ");
            }
            sb.deleteCharAt(sb.length() - 1);
            String cinemaName = sb.toString();
            for (Show s : shows)
                if (cinemaName.equals(s.getCinemaName()) && date.compareTo(s.getDate()) == 0) {
                    requiredShow = s;
                    println("Инофрмация о фильме " + filmName + " в кинотеатре " + cinemaName + " " + words[words.length - 2] + " в " + words[words.length - 1] + ":");
                    for (int i = 1; i < s.getSeats().length; i++) {
                        print("Ряд " + i + "   ");
                        for (int j = 0; j < s.getSeats()[i].length; j++)
                            print(s.getSeats()[i][j] + " ");
                        println("");
                    }
                    println("0 означает, что место уже забронировано.\nДля бронирования мест введите booking n1,m1 n2,m2...\nгде n- номер ряда, m- номер места.");
                }


        } catch (ParseException e) {
            print("Не удалось получить информацию о фильме.");
        }
    }

    private void getFilm(String[] words) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < words.length; i++) {
            sb.append(words[i]);
            sb.append(" ");
        }
        sb.deleteCharAt(sb.length() - 1);
        Shows shows;
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yy HH:mm");
        try {
            shows = tW.getFilm(sb.toString(), false);
            filmName = shows.getFilmName();
            println("Фильм " + filmName + " показывают:");
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
        for (int i = 1; i < words.length - 1; i++) {
            sb.append(words[i]);
            sb.append(" ");
        }
        sb.deleteCharAt(sb.length() - 1);
        String name = sb.toString();
        token = sH.signIn(name, password);
        if (token.getKey()) {
            println("Вход в систему выполнен успешно.\nДля выхода из Вашего аккаунта введите signout\n" +
                    "Для получения списка забронированных Вами мест введите tickets");

            userName = name;
        }
        else println("Не удалось выполнить вход. Проверьте имя и пароль.");
    }

    private void getFilms() {
        HashMap<String, Pair<Date, Date>> map = null;
        try {
            map = tW.getFilms();
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
        boolean isSuccessful = sH.signUp(name, password);
        if (isSuccessful) println("Новый пользователь зарегистрирован.");
        else println("Не удалось создать нового пользователя. Возможно пользователь с таким именем уже существует.");
    }

    private void printIncorrectData() {
        println("Введены некорректные данные. Повторите ввод.");
    }

    public static void main(String[] args) {
        ConsoleUserInterface CUI = new ConsoleUserInterface();
        CUI.printGreeting();
        CUI.readMessage();
    }


}
