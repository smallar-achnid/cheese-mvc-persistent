package org.launchcode.controllers;

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
@RequestMapping("menu")
public class MenuController {
    @Autowired
    private CheeseDao cheeseDao;

    @Autowired
    private MenuDao menuDao;

    @RequestMapping(value = "")
    public String index(Model model) {

        model.addAttribute("menus", menuDao.findAll());
        model.addAttribute("title", "My Menus");

        return "menu/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String displayAddMenuForm(Model model) {
        model.addAttribute("title", "Add Menu");
        model.addAttribute(new Menu());
        //model.addAttribute("categories", categoryDao.findAll());
        return "menu/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String processAddMenuForm(Model model,
            @ModelAttribute @Valid Menu newMenu,
            Errors errors
            ) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Menu");
            return "menu/add";
        }

        menuDao.save(newMenu);
        return "redirect:view/" + newMenu.getId();
    }

        @RequestMapping(value = "view/{menuId}", method = RequestMethod.GET)
        public String viewMenu(Model model, @PathVariable int menuId) {
            Menu menu = menuDao.findOne(menuId);
            //AddMenuItemForm form = new AddMenuItemForm(menu, cheeseDao.findAll());
            model.addAttribute("title", menu.getName());
            model.addAttribute("cheeses", menu.getCheeses());
            model.addAttribute("menuId", menu.getId());

            return "menu/view";
        }

        @RequestMapping(value = "add-item/{menuId}", method = RequestMethod.GET)
        public String addItem(Model model, @PathVariable int menuId) {

            Menu menu = menuDao.findOne(menuId);
            Iterable<Cheese> cheeses = cheeseDao.findAll();
            AddMenuItemForm form = new AddMenuItemForm(menu, cheeses);
            model.addAttribute("form", form);
            model.addAttribute("title", "Add item to menu:" + menu.getName());
            //model.addAttribute(menu);
            return "menu/add-item";
        }

    @RequestMapping(value = "add-item", method = RequestMethod.POST)
    public String addItem(Model model,
                          @ModelAttribute @Valid AddMenuItemForm form,
                          Errors errors ) {

        if(errors.hasErrors()) {
            model.addAttribute("form", form);
            return "menu";
        }

        Cheese theCheese = cheeseDao.findOne(form.getCheeseId());
        Menu theMenu = menuDao.findOne(form.getMenuId());
        theMenu.addItem(theCheese);
        menuDao.save(theMenu);
        return "redirect:/menu/view/" + theMenu.getId();
    }

    }

