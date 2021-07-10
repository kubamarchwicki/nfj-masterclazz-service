Feature: Process customer login information

  Background:
    Given A GovAudit system integration


  Scenario: A successful customer login
    Given A customer exists
    When A customer logged-in event is emitted
    Then GovAudit system is notified with user data