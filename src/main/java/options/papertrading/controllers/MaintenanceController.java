package options.papertrading.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/maintenance")
public class MaintenanceController {

    @GetMapping
    public String showMaintenancePage() {
        return "maintenance/maintenance";
    }
}
