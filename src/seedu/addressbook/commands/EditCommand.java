package seedu.addressbook.commands;

import java.util.HashSet;
import java.util.Set;

import seedu.addressbook.common.Messages;
import seedu.addressbook.data.exception.IllegalValueException;
import seedu.addressbook.data.person.Address;
import seedu.addressbook.data.person.Email;
import seedu.addressbook.data.person.Name;
import seedu.addressbook.data.person.Person;
import seedu.addressbook.data.person.Phone;
import seedu.addressbook.data.person.ReadOnlyPerson;
import seedu.addressbook.data.person.UniquePersonList.DuplicatePersonException;
import seedu.addressbook.data.person.UniquePersonList.PersonNotFoundException;
import seedu.addressbook.data.tag.Tag;
import seedu.addressbook.data.tag.UniqueTagList;

/**
 * Represents the command to edit a person,
 * identified using the last displayed index in the address book.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n" + "Edits the person identified by the index number used in the last person listing. "
            + "Contact details can be marked private by prepending 'p' to the prefix.\n\t"
            + "Details that are not provided are not changed.\n\t"
            + "Parameters: INDEX [NAME] [[p]p/PHONE] [[p]e/EMAIL] [[p]a/ADDRESS]  [[t/TAG]...]\n\t"
            + "Example: " + COMMAND_WORD
            + " John Doe p/98765432 e/johnd@gmail.com a/311, Clementi Ave 2, #02-25 t/friends t/owesMoney";

    public static final String MESSAGE_DUPLICATE_PERSON = "The edited person already exists in the address book";

    public static final String MESSAGE_SUCCESS = "Edited person to: %1$s";
    
    private final Name newName;
    private final Phone newPhone;
    private final Email newEmail;
    private final Address newAddress;
    private final Set<Tag> newTags;

    /**
     * Convenience constructor using raw values.
     * Parameters newName, newPhone, newEmail, newAddress and newTags can be null.
     * If they are null, the old values of the person will be used. If not, they
     * will be used to replaced the current values.
     * 
     * @throws IllegalValueException if any of the raw values are not null and yet invalid
     */
    public EditCommand(int targetVisibleIndex,
                      String newName,
                      String newPhone, boolean isPhonePrivate,
                      String newEmail, boolean isEmailPrivate,
                      String newAddress, boolean isAddressPrivate,
                      Set<String> newTags) throws IllegalValueException {
        super(targetVisibleIndex);
        
        if (newName != null) this.newName = new Name(newName);
        else this.newName = null;
        if (newPhone != null) this.newPhone = new Phone(newPhone, isPhonePrivate);
        else this.newPhone = null;
        if (newEmail != null) this.newEmail = new Email(newEmail, isEmailPrivate);
        else this.newEmail = null;
        if (newAddress != null) this.newAddress = new Address(newAddress, isAddressPrivate);
        else this.newAddress = null;
        
        if (newTags != null) {
            this.newTags = new HashSet<>();
            for (String tagName : newTags) {
                this.newTags.add(new Tag(tagName));
            }
        } else {
            this.newTags = null;
        }
    }
    
    @Override
    public CommandResult execute() {
        try {
            final ReadOnlyPerson target = getTargetPerson();
            final Person replacement = new Person(
                    newName == null ? target.getName() : newName,
                    newPhone == null ? target.getPhone() : newPhone,
                    newEmail == null ? target.getEmail() : newEmail,
                    newAddress == null ? target.getAddress() : newAddress,
                    newTags == null ? target.getTags() : new UniqueTagList(newTags)
            );
            
            addressBook.replacePerson(target, replacement);
            return new CommandResult(String.format(MESSAGE_SUCCESS, replacement));
        } catch (IndexOutOfBoundsException ie) {
            return new CommandResult(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        } catch (DuplicatePersonException e) {
            return new CommandResult(MESSAGE_DUPLICATE_PERSON);
        } catch (PersonNotFoundException e) {
            return new CommandResult(Messages.MESSAGE_PERSON_NOT_IN_ADDRESSBOOK);
        }
    }

}
