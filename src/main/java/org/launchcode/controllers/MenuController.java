package org.launchcode.controllers;

import org.launchcode.models.Category;
import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping (value = "menu")
public class MenuController {
    @Autowired
    private CheeseDao cheeseDao;

    @Autowired
    private MenuDao menuDao;

    @RequestMapping(value = "")
    public String index(Model model) {
        model.addAttribute("menus", menuDao.findAll());
        model.addAttribute("title", "Menus");

        return "menu/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String displayAddMenuForm(Model model) {
        model.addAttribute("title", "Add Menu");
        model.addAttribute(new Menu());
        model.addAttribute("categories", menuDao.findAll());
        return "menu/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String processAddMenuForm(@ModelAttribute @Valid Menu newMenu,
                                       Errors errors, Model model) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Menu");
            return "menu/add";
        }

        menuDao.save(newMenu);

        return "redirect:view/" + newMenu.getId(); // in instructions says menu.getId()
    }

    @RequestMapping(value = "view/{id}", method = RequestMethod.GET)
    public String viewMenu(Model model, @PathVariable int id) {

        Menu thisMenu = menuDao.findOne(id);

        model.addAttribute("menu", thisMenu);
        model.addAttribute("title", thisMenu.getName());

        return "menu/view";

    }

    @RequestMapping(value = "add-item/{id}", method = RequestMethod.GET)
    public String addItem(Model model, @PathVariable int id) {

        Menu menu = menuDao.findOne(id);

        AddMenuItemForm newForm = new AddMenuItemForm(menu, cheeseDao.findAll());

        model.addAttribute("form", newForm);
        model.addAttribute("title", "Add Item to Menu: " + menu.getName());

        return "menu/add-item";

    }

    @RequestMapping(value = "add-item/{id}", method = RequestMethod.POST)
    public String processAddMenuForm(@ModelAttribute @Valid AddMenuItemForm newForm,
                                     Errors errors, Model model) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Item");
            model.addAttribute("form", newForm);
            return "menu/add-item";
        }

        Cheese thisCheese = cheeseDao.findOne(newForm.getCheeseId());
        Menu thisMenu = menuDao.findOne(newForm.getMenuId());

        thisMenu.addItem(thisCheese);

        menuDao.save(thisMenu);

        return "redirect:/menu/view/" + thisMenu.getId(); // in instructions says menu.getId()
    }




}
