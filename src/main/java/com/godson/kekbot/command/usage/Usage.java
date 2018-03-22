package com.godson.kekbot.command.usage;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.godson.kekbot.ThrowableString;
import com.godson.kekbot.command.usage.Usage.UsageOverload;

/**
 * Converts usage strings into objects to compare against later
 *
 * The usage system is based on Klasa's. https://github.com/dirigeants/klasa
 */
public class Usage extends ArrayList<UsageOverload> {

    // Tag opening/closing/spacing characters
    protected static final String OPEN = "[(<";
    protected static final String CLOSE = "])>";
    protected static final String SPACE = " \n";

    public Usage() {}

    public Usage add(String usageString, String usageDelim) throws ParseException, ThrowableString {
        super.add(new UsageOverload(usageString, usageDelim));
        return this;
    }

    /**
     * Represents each overload in this usage
     */
    public static class UsageOverload {
        /**
         * The usage string, re-deliminated with the usageDelim
         */
        public String deliminatedUsage = "";
        /**
         * The original usage string, passed from the command constructor
         */
        public String usageString;
        /**
         * The delimiter used between parameters
         */
        public String usageDelim;
        /**
         * The actual parsed usage objects, one for each tag
         */
        public Tag[] parsedUsage;

        public UsageOverload(String usageString, String usageDelim) throws ParseException, ThrowableString {
            if (!usageString.isEmpty()) {
                deliminatedUsage = " " + String.join(usageDelim, usageString.split(" "));
            }
            this.usageString = usageString;
            this.usageDelim = usageDelim;
            parsedUsage = parseUsage(this.usageString);
        }

        @Override
        public String toString() {
            return this.deliminatedUsage;
        }

        /**
         * Method responsible for building the usage overload object to check against
         */
        public static Tag[] parseUsage(String usageString) throws ParseException, ThrowableString {
            final ParserData usage = new ParserData();
            final char[] usageChars = usageString.toCharArray();

            for (int i = 0; i < usageChars.length; i++) {
                final char c = usageChars[i];
                usage.charIndex = i + 1;
                usage.from = usage.charIndex - usage.current.length();
                usage.at = String.format("at char #%d '%s'", usage.charIndex, c);
                usage.fromTo = String.format("from char #%d to #%d '%s'", usage.from, usage.charIndex, usage.current);

                if (usage.last && c == ' ') throw new ParseException(usage.at + ": there can't be anything else after the repeat tag.", usage.charIndex);

                if (c == '/' && usage.current.charAt(usage.current.length() - 1) != '\\') usage.openRegex = !usage.openRegex;

                if (usage.openRegex) {
                    usage.current += c;
                    continue;
                }

                if (OPEN.indexOf(c) > -1) tagOpen(usage, c);
                else if (CLOSE.indexOf(c) > -1) tagClose(usage, c);
                else if (SPACE.indexOf(c) > -1) tagSpace(usage, c);
                else usage.current += c;
            }

            if (usage.opened != 0) {
                final int position = usageString.length() - usage.current.length();
                throw new ParseException(String.format(
                    "from char #%d '%s' to end: a tag was left open",
                    position,
                    usageString.substring(position - 1)
                ), position);
            }
            if (usage.current.length() > 0) {
                final int position = (usageString.length() + 1) - usage.current.length();
                throw new ParseException(String.format(
                    "from char #%d to end '%s' a literal was found outside a tag.",
                    position,
                    usage.current
                ), position);
            }

            return usage.tags.toArray(new Tag[usage.tags.size()]);
        }

        protected static class ParserData {
            public List<Tag> tags = new ArrayList<>();
            public int opened = 0;
            public String current = "";
            public boolean openRegex = false;
            public Tag.Required openReq = Tag.Required.OPTIONAL;
            public boolean last = false;
            public int charIndex = 0;
            public int from = 0;
            public String at = "";
            public String fromTo = "";
        }

        /**
         * Method responsible for handling tag opens
         */
        private static void tagOpen(ParserData usage, char c) throws ParseException {
            if (usage.opened != 0) throw new ParseException(usage.at + ": you may not open a tag inside another tag.", usage.charIndex);
            if (usage.current.length() > 0) throw new ParseException(usage.fromTo + ": there can't be a literal outside a tag", usage.from);
            usage.opened++;
            usage.openReq = Tag.Required.values()[OPEN.indexOf(c)];
        }

        /**
         * Method responsible for handling tag closes
         */
        private static void tagClose(ParserData usage, char c) throws ParseException, ThrowableString {
            final Tag.Required required = Tag.Required.values()[CLOSE.indexOf(c)];
            if (usage.opened == 0) throw new ParseException(usage.at + ": invalid close tag found", usage.charIndex);
            if (usage.openReq != required) throw new ParseException(String.format(
                "%s: Invalid closure of '%s%s' with '%s'",
                usage.at, OPEN.charAt(usage.openReq.ordinal()), usage.current, CLOSE.charAt(required.ordinal())
            ), usage.charIndex);
            if (usage.current.isEmpty()) throw new ParseException(usage.at + ": empty tag found", usage.charIndex);
            usage.opened--;
            if (usage.current.equals("...")) {
                if (usage.openReq != Tag.Required.OPTIONAL) throw new ParseException(usage.at + ": repeat tag cannot be required", usage.charIndex);
                if (usage.tags.size() < 1) throw new ParseException(usage.fromTo + ": there can't be a repeat at the beginning", usage.from);
                usage.tags.get(usage.tags.size() - 1).repeat = true;
                usage.last = true;
            } else {
                usage.tags.add(new Tag(usage.current, usage.tags.size() + 1, required));
            }
            usage.current = "";
        }

        /**
         * Method responsible for handling tag spacing
         */
        private static void tagSpace(ParserData usage, char c) throws ParseException {
            if (c == '\n') throw new ParseException(usage.at + ": there can't be a line break in the usage string", usage.charIndex);
            if (usage.opened != 0) throw new ParseException(usage.at + ": spaces aren't allowed inside a tag", usage.charIndex);
            if (usage.current.length() > 0) throw new ParseException(usage.fromTo + ": there can't be a literal outside a tag.", usage.from);
        }
    }
}
