/**
 * Copyright (c) 2018 Andrea Di Giorgi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.ithildir.frontpage.model;

import java.time.LocalDate;

import java.util.List;
import java.util.Map;

/**
 * @author Andrea Di Giorgi
 */
public interface User {

	public LocalDate getBirthday();

	public List<String> getCauses();

	public String getEmailAddress();

	public String getFirstName();

	public String getFullName();

	public String getHeadline();

	public Map<String, String> getInstantMessengers();

	public Map<String, String> getLanguages();

	public String getLastName();

	public Map<String, String> getLinks();

	public Location getLocation();

	public List<String> getSkills();

	public String getSummary();

	public String getUserId();

	public boolean isMale();

	public void setBirthday(LocalDate birthday);

	public void setCauses(List<String> causes);

	public void setEmailAddress(String emailAddress);

	public void setFirstName(String firstName);

	public void setFullName(String fullName);

	public void setHeadline(String headline);

	public void setInstantMessengers(Map<String, String> instantMessengers);

	public void setLanguages(Map<String, String> languages);

	public void setLastName(String lastName);

	public void setLinks(Map<String, String> links);

	public void setMale(boolean male);

	public void setSkills(List<String> skills);

	public void setSummary(String summary);

}