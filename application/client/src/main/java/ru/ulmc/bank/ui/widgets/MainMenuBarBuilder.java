package ru.ulmc.bank.ui.widgets;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Sizeable;
import com.vaadin.spring.navigator.SpringNavigator;
import com.vaadin.ui.*;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.ulmc.bank.ui.MainUI;
import ru.ulmc.bank.ui.views.HomeView;
import ru.ulmc.bank.ui.views.charts.QuotesView;
import ru.ulmc.bank.ui.views.monitoring.QuotesMonitoringView;
import ru.ulmc.bank.ui.views.settings.SymbolsView;;
import ru.ulmc.bank.ui.widgets.util.MenuSupport;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Строитель верхней понели
 */
public class MainMenuBarBuilder {
    private VerticalLayout verticalLayout;

    private LinkedList<MenuBar.MenuItem> lastItems = new LinkedList<>();
    private Map<String, MenuBar.MenuItem> viewNameToItem = new HashMap<>();
    private Map<String, MenuSupport> viewNameToMenuSupport = new HashMap<>();

    private MenuBar lastActionsBar;

    private MenuBar mainMenu;
    private MenuBar rightMenu;

    private boolean recentMenuBarShown = false;

    private SpringNavigator navigator;

    public MainMenuBarBuilder(SpringNavigator navigator) {
        this.navigator = navigator;
        mainMenu = new MenuBar();
        mainMenu.addStyleName("square-and-flat");
        mainMenu.setWidth(100, Sizeable.Unit.PERCENTAGE);
        mainMenu.setAutoOpen(true);

        lastActionsBar = new MenuBar();
        lastActionsBar.addStyleName("recent-menu");
        // lastActionsBar.setHeight("26px");
        lastActionsBar.setVisible(recentMenuBarShown);
        lastActionsBar.setWidth(100, Sizeable.Unit.PERCENTAGE);
        setupMainMenuItems();
    }

    public boolean haveView(String viewName) {
        return viewNameToItem.keySet().contains(viewName);
    }

    public MainMenuBarBuilder addReferenceGroup() {
        setupReferenceMenu();
        return this;
    }

    public MainMenuBarBuilder addOtherGroups() {
        setupMenuItems();
        return this;
    }

    public MainMenuBarBuilder addRightMenu(String logoutUrl, String userName, final MainUI ui) {
        rightMenu = new MenuBar();
        rightMenu.addStyleNames("square-and-flat", "fixed-min-icon-size");
        setupRightMenuItems(logoutUrl, userName, ui);
        return this;
    }

    public AbstractLayout build() {
        setupFinalLayout();
        disableInitialStateMenuItem();
        return verticalLayout;
    }

    private void setupFinalLayout() {
        GridLayout gridLayout = new GridLayout();
        gridLayout.setWidth(100, Sizeable.Unit.PERCENTAGE);
        gridLayout.setColumns(2);
        gridLayout.setRows(1);
        gridLayout.addComponent(mainMenu, 0, 0);
        gridLayout.addComponent(rightMenu, 1, 0);
        gridLayout.setComponentAlignment(rightMenu, Alignment.TOP_RIGHT);
        gridLayout.setColumnExpandRatio(0, 10f);
        gridLayout.setColumnExpandRatio(1, 0);
        gridLayout.setResponsive(true);
        verticalLayout = new VerticalLayout(gridLayout, lastActionsBar);
        verticalLayout.setWidth(100, Sizeable.Unit.PERCENTAGE);
        verticalLayout.setStyleName("no-padding");
        verticalLayout.setSpacing(false);
    }

    private void disableInitialStateMenuItem() {
        if (navigator == null || navigator.getState() == null)
            return;
        MenuBar.MenuItem item = viewNameToItem.get(navigator.getState());
        if (item != null) {
            item.setEnabled(false);
        }
    }

    private void setupMainMenuItems() {
        MenuBar.MenuItem brand = mainMenu.addItem("APaRM AS", VaadinIcons.MONEY_EXCHANGE,
                selectedItem -> commonMenuActions(selectedItem, HomeView.MENU_SUPPORT));
        commonActionWithMenuSupport(brand, HomeView.MENU_SUPPORT);
        brand.setEnabled(false);
        brand.setStyleName("brand");
        brand.setDescription("Analysis Processing and Rates Management Automated System");
    }

    private void setupMenuItems() {
        // mainMenu.addItem("Menu 2", null, null);
        // mainMenu.addItem("Menu 3", null, null);
    }

    private void setupRightMenuItems(String logoutUrl, String userName, final MainUI ui) {
        MenuBar.MenuItem user = rightMenu.addItem(userName, VaadinIcons.USER, null);
        user.setEnabled(false);
        user.setDescription("Имя пользователя");

        MenuBar.MenuItem recentMenu = rightMenu.addItem("", VaadinIcons.CLOCK, this::toggleRecentMenuBar);
        recentMenu.setCheckable(true);
        recentMenu.setChecked(recentMenuBarShown);
        recentMenu.setDescription("Отображение последних переходов");

        rightMenu.addItem("Выйти", VaadinIcons.SIGN_OUT,
                selectedItem -> {
                    SecurityContextHolder.clearContext();
                    ui.getSession().getSession().invalidate();
                    ui.getSession().close();
                    ui.getPage().setLocation(logoutUrl);
                });
    }

    private void toggleRecentMenuBar(MenuBar.MenuItem switcher) {
        recentMenuBarShown = switcher.isChecked();
        lastActionsBar.setVisible(recentMenuBarShown);
        addToLastItemsMenuBar(null);
    }

    private void setupReferenceMenu() {
        MenuBar.MenuItem refMenu = mainMenu.addItem("Справочники", VaadinIcons.BOOK, null);
        createSubmenuItem(refMenu, SymbolsView.MENU_SUPPORT);

        //refMenu = mainMenu.addItem("Отчеты", VaadinIcons.CLIPBOARD_TEXT, null);

        refMenu = mainMenu.addItem("Мониторинг", VaadinIcons.DASHBOARD, null);
        createSubmenuItem(refMenu, QuotesView.MENU_SUPPORT);
        createSubmenuItem(refMenu, QuotesMonitoringView.MENU_SUPPORT);

       // refMenu = mainMenu.addItem("Настройки", VaadinIcons.COG, null);
    }

    private void createSubmenuItem(MenuBar.MenuItem parent, MenuSupport menuSupport) {
        commonActionWithMenuSupport(parent.addItem(
                menuSupport.getTitle(), item -> commonMenuActions(item, menuSupport)), menuSupport);
    }

    private void commonActionWithMenuSupport(MenuBar.MenuItem item, MenuSupport menuSupport) {
        viewNameToItem.put(menuSupport.getName(), item);
        viewNameToMenuSupport.put(menuSupport.getName(), menuSupport);
    }

    public MenuSupport getMenuSupport(String viewName) {
        return viewNameToMenuSupport.get(viewName);
    }

    private void commonMenuActions(MenuBar.MenuItem item, MenuSupport menuSupport) {
        if (menuSupport.getName().equals(navigator.getState())) {
            return;
        }
        viewNameToItem.get(navigator.getState()).setEnabled(true);
        item.setEnabled(false);

        navigator.navigateTo(menuSupport.getName());
        navigator.getCurrentView().getViewComponent().getParent().setCaption(menuSupport.getTitle());
        addToLastItemsMenuBar(item);
    }

    private void addToLastItemsMenuBar(MenuBar.MenuItem item) {
        if (item != null) {
            lastItems.remove(findCandidate(lastItems, item));
        }
        if (recentMenuBarShown) {
            lastActionsBar.removeItems();
            for (MenuBar.MenuItem i : lastItems) {
                lastActionsBar.addItem(i.getText(), i.getIcon(), i.getCommand());
            }
        }
        if (item != null) {
            lastItems.add(0, item);
        }
        while (lastItems.size() > 12) {
            lastItems.remove(lastItems.size() - 1);
        }
    }

    private MenuBar.MenuItem findCandidate(List<MenuBar.MenuItem> children, MenuBar.MenuItem candidate) {
        for (MenuBar.MenuItem item : children) {
            if (item.getText().equals(candidate.getText())) {
                return item;
            }
        }
        return null;
    }
}
